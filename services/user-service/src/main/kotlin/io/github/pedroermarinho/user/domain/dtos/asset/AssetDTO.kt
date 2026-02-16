package io.github.pedroermarinho.user.domain.dtos.asset

import io.github.pedroermarinho.user.domain.enums.FilePathEnum
import io.github.pedroermarinho.user.domain.enums.StorageProviderEnum
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class AssetDTO(
    val id: EntityId,
    val storageProvider: StorageProviderEnum,
    val bucketName: String,
    val fileExtension: String,
    val fileSizeBytes: Long,
    val storagePath: FilePathEnum,
    val tags: List<String>?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
