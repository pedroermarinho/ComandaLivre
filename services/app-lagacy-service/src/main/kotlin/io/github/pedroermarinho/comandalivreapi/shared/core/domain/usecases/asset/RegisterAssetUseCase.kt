package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset

import com.github.f4b6a3.uuid.UuidCreator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.AssetEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.StorageProviderEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.AssetRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.S3Service
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties.S3Properties
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.FileTypes
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.convertMultiPartToFile
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.validateImageFile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files

@Transactional
@UseCase
class RegisterAssetUseCase(
    private val assetRepository: AssetRepository,
    private val s3Service: S3Service,
    private val s3Properties: S3Properties,
) {
    private val log = KotlinLogging.logger {}

    private companion object {
        const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024 // Exemplo: 10MB
    }

    data class AssetUploadParams(
        val file: MultipartFile,
        val storagePath: FilePathEnum,
        val storageProvider: StorageProviderEnum = StorageProviderEnum.S3_AWS,
        val tags: List<String>? = null,
    )

    fun execute(params: AssetUploadParams): Result<EntityId> {
        log.info { "Iniciando registro de novo asset para o caminho: ${params.storagePath.value}" }

        if (params.file.isEmpty) {
            log.warn { "Tentativa de upload de arquivo vazio para ${params.storagePath.value}." }
            return Result.failure(BusinessLogicException("O arquivo não pode estar vazio."))
        }

        if (params.file.size > MAX_FILE_SIZE_BYTES) {
            log.warn { "Arquivo excede o tamanho máximo permitido de ${MAX_FILE_SIZE_BYTES / (1024 * 1024)}MB." }
            return Result.failure(BusinessLogicException("O arquivo excede o tamanho máximo permitido."))
        }

        validateImageFile(params.file, listOf(FileTypes.JPG, FileTypes.PNG)).onFailure {
            return Result.failure(it)
        }

        val contentType = params.file.contentType

        val publicId = UuidCreator.getTimeOrderedEpoch()
        val originalFilename = params.file.originalFilename ?: publicId.toString()
        val fileExtension =
            originalFilename
                .substringAfterLast('.', "")
                .ifBlank {
                    when (contentType) {
                        "image/png" -> "png"
                        "image/jpeg" -> "jpg"
                        "image/gif" -> "gif"
                        "application/pdf" -> "pdf"
                        else -> throw BusinessLogicException("Tipo de arquivo não suportado.")
                    }
                }.lowercase()

        val s3FileNameWithExtension = "$publicId.$fileExtension"
        var tempFile: File? = null

        try {
            tempFile = convertMultiPartToFile(params.file, s3FileNameWithExtension)
            log.debug { "Arquivo temporário ${tempFile.name} criado para upload." }
            return s3Service.uploadFile(tempFile, params.storagePath, s3FileNameWithExtension).fold(
                onSuccess = { s3ObjectKey ->
                    log.info { "Upload para S3 bem-sucedido. Chave S3: $s3ObjectKey, publicId: $publicId" }

                    assetRepository
                        .save(
                            AssetEntity.createNew(
                                publicId = publicId,
                                storageProvider = params.storageProvider,
                                bucketName = s3Properties.bucket,
                                fileExtension = fileExtension,
                                fileSizeBytes = params.file.size,
                                storagePath = params.storagePath,
                                tags = params.tags,
                            ),
                        ).fold(
                            onSuccess = { asset ->
                                log.info { "Metadados do asset (publicId: $publicId) salvos no banco de dados." }
                                Result.success(asset)
                            },
                            onFailure = { dbError ->
                                log.error(dbError) { "Falha ao salvar metadados do asset $publicId no banco. Tentando reverter upload S3." }
                                s3Service
                                    .deleteFile(s3FileNameWithExtension, params.storagePath)
                                    .onFailure { s3DeleteError ->
                                        log.error(s3DeleteError) { "Falha crítica ao deletar $s3ObjectKey do S3 após erro no DB." }
                                    }
                                Result.failure(BusinessLogicException("Erro ao registrar o asset."))
                            },
                        )
                },
                onFailure = { s3Error ->
                    log.error(s3Error) { "Falha no upload para S3 do arquivo $s3FileNameWithExtension." }
                    Result.failure(BusinessLogicException("Erro no serviço de upload de arquivo."))
                },
            )
        } catch (e: Exception) {
            log.error(e) { "Erro inesperado durante o processo de registro do asset." }
            return Result.failure(BusinessLogicException("Erro inesperado ao processar o arquivo."))
        } finally {
            tempFile?.let {
                try {
                    Files.deleteIfExists(it.toPath())
                    log.debug { "Arquivo temporário ${it.name} deletado." }
                } catch (e: Exception) {
                    log.warn(e) { "Não foi possível deletar o arquivo temporário ${it.name}." }
                }
            }
        }
    }
}
