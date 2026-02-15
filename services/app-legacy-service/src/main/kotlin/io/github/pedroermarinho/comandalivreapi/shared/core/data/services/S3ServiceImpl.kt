package io.github.pedroermarinho.comandalivreapi.shared.core.data.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureSystemFlagEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.FeatureException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.S3Service
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.featureflag.StatusFeatureFlagUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties.S3Properties
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.File
import java.time.Duration

@Service
class S3ServiceImpl(
    private val s3Properties: S3Properties,
    private val statusFeatureFlagUseCase: StatusFeatureFlagUseCase,
) : S3Service {
    private val log = KotlinLogging.logger {}
    private val s3Client: S3Client = createS3Client()
    private val s3Presigner: S3Presigner = createS3Presigner()

    private fun createS3Client(): S3Client =
        S3Client
            .builder()
            .region(Region.of(s3Properties.region))
            .endpointOverride(java.net.URI(s3Properties.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        s3Properties.accessKey,
                        s3Properties.secretKey,
                    ),
                ),
            ).build()

    private fun createS3Presigner(): S3Presigner =
        S3Presigner
            .builder()
            .region(Region.of(s3Properties.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        s3Properties.accessKey,
                        s3Properties.secretKey,
                    ),
                ),
            ).build()

    override fun uploadFile(
        file: File,
        path: FilePathEnum,
        fileName: String,
    ): Result<String> {
        if (!statusFeatureFlagUseCase.isEnabled(FeatureSystemFlagEnum.S3_INTEGRATION)) {
            return Result.failure(FeatureException("Integração com S3 desabilitada"))
        }

        val key = buildS3Key(fileName, path)

        return try {
            val request =
                PutObjectRequest
                    .builder()
                    .bucket(s3Properties.bucket)
                    .key(key)
                    .build()

            s3Client.putObject(request, RequestBody.fromFile(file))
            return Result.success(key)
        } catch (e: S3Exception) {
            log.error(e) { "Erro ao fazer upload do arquivo para o S3" }
            Result.failure(e)
        }
    }

    override fun deleteFile(
        fileName: String,
        path: FilePathEnum,
    ): Result<Unit> {
        val key = buildS3Key(fileName, path)

        try {
            val request =
                DeleteObjectRequest
                    .builder()
                    .bucket(s3Properties.bucket)
                    .key(key)
                    .build()

            s3Client.deleteObject(request)
            return Result.success(Unit)
        } catch (e: S3Exception) {
            return Result.failure(e)
        }
    }

    override fun generatePresignedUrl(
        fileName: String,
        path: FilePathEnum,
    ): Result<String> {
        if (!statusFeatureFlagUseCase.isEnabled(FeatureSystemFlagEnum.S3_INTEGRATION)) {
            return Result.failure(FeatureException("Integração com S3 desabilitada"))
        }

        if (fileName.isBlank()) {
            return Result.failure(BusinessLogicException("Nome do arquivo não pode ser vazio"))
        }

        val key = buildS3Key(fileName, path)
        val expiration = Duration.ofDays(7)

        return try {
            val presignedUrl =
                s3Presigner
                    .presignGetObject(
                        GetObjectPresignRequest
                            .builder()
                            .signatureDuration(expiration)
                            .getObjectRequest(
                                GetObjectRequest
                                    .builder()
                                    .bucket(s3Properties.bucket)
                                    .key(key)
                                    .build(),
                            ).build(),
                    ).url()
            Result.success(presignedUrl.toString())
        } catch (e: S3Exception) {
            log.error(e) { "Erro ao gerar URL pré-assinada para o arquivo: $key" }
            Result.failure(BusinessLogicException("Erro ao gerar URL pré-assinada"))
        }
    }

    private fun buildS3Key(
        fileName: String,
        path: FilePathEnum,
    ): String = "${path.value}/$fileName"
}
