package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.asset.AssetDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.AssetEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.StorageProviderEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.AssetsRecord

@Component
class AssetPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: AssetEntity): Result<AssetsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            AssetsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                storageProvider = entity.storageProvider.name,
                bucketName = entity.bucketName,
                fileExtension = entity.fileExtension,
                fileSizeBytes = entity.fileSizeBytes,
                storagePath = entity.storagePath.name,
                tags = entity.tags?.toTypedArray(),
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: AssetsRecord): Result<AssetEntity> =
        errorDataConversion {
            AssetEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                storageProvider = StorageProviderEnum.fromString(record.storageProvider),
                bucketName = record.bucketName,
                fileExtension = record.fileExtension,
                fileSizeBytes = record.fileSizeBytes,
                storagePath = FilePathEnum.fromString(record.storagePath),
                tags = record.tags?.filterNotNull()?.toList(),
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}

@Component
class AssetMapper {
    fun toDTO(entity: AssetEntity): AssetDTO =
        AssetDTO(
            id = entity.id,
            storageProvider = entity.storageProvider,
            bucketName = entity.bucketName,
            fileExtension = entity.fileExtension,
            fileSizeBytes = entity.fileSizeBytes,
            storagePath = entity.storagePath,
            tags = entity.tags,
            createdAt = entity.audit.createdAt,
            updatedAt = entity.audit.updatedAt,
        )
}
