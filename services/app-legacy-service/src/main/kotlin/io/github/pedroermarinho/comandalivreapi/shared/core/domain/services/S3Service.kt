package io.github.pedroermarinho.comandalivreapi.shared.core.domain.services

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FilePathEnum
import java.io.File

interface S3Service {
    fun uploadFile(
        file: File,
        path: FilePathEnum,
        fileName: String,
    ): Result<String>

    fun deleteFile(
        fileName: String,
        path: FilePathEnum,
    ): Result<Unit>

    fun generatePresignedUrl(
        fileName: String,
        path: FilePathEnum,
    ): Result<String>
}
