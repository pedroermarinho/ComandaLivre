package io.github.pedroermarinho.user.domain.usecases.asset

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.asset.AssetDTO
import io.github.pedroermarinho.user.domain.enums.StorageProviderEnum
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.repositories.AssetRepository
import io.github.pedroermarinho.user.domain.services.S3Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class GetUrlAssetUseCase(
    private val s3Service: S3Service,
    private val assetRepository: AssetRepository,
) {
    fun execute(asset: AssetDTO): Result<String> {
        if (asset.storageProvider != StorageProviderEnum.S3_AWS) {
            return Result.failure(BusinessLogicException("Serviço não suportado."))
        }

        val s3FileName = "${asset.id.publicId}.${asset.fileExtension}"
        return s3Service.generatePresignedUrl(
            fileName = s3FileName,
            path = asset.storagePath,
        )
    }

    fun execute(assetId: Int): Result<String> {
        val asset =
            assetRepository.getById(assetId).getOrElse {
                return Result.failure(it)
            }
        if (asset.storageProvider != StorageProviderEnum.S3_AWS) {
            return Result.failure(BusinessLogicException("Serviço não suportado."))
        }

        val s3FileName = "${asset.id.publicId}.${asset.fileExtension}"
        return s3Service.generatePresignedUrl(
            fileName = s3FileName,
            path = asset.storagePath,
        )
    }
}
