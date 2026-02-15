package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.StorageProviderEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class AssetEntity(
    val id: EntityId,
    val storageProvider: StorageProviderEnum,
    val bucketName: String,
    val fileExtension: String,
    val fileSizeBytes: Long,
    val storagePath: FilePathEnum,
    val tags: List<String>?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            storageProvider: StorageProviderEnum,
            bucketName: String,
            fileExtension: String,
            fileSizeBytes: Long,
            storagePath: FilePathEnum,
            tags: List<String>?,
        ): AssetEntity =
            AssetEntity(
                id = EntityId.createNew(publicId = publicId),
                storageProvider = storageProvider,
                bucketName = bucketName,
                fileExtension = fileExtension,
                fileSizeBytes = fileSizeBytes,
                storagePath = storagePath,
                tags = tags,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
