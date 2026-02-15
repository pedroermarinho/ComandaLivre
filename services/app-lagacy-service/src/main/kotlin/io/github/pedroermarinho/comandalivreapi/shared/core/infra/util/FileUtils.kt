package io.github.pedroermarinho.comandalivreapi.shared.core.infra.util

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

const val TEMP_DIR = "java.io.tmpdir"

enum class FileTypes(
    val contentType: String,
) {
    PNG("image/png"),
    JPEG("image/jpeg"),
    JPG("image/jpg"),
    GIF("image/gif"),
    SVG("image/svg+xml"),
    WEBP("image/webp"),
    ;

    companion object {
        fun fromContentType(contentType: String): FileTypes? = entries.find { it.contentType == contentType }
    }
}

fun validateImageFile(
    file: MultipartFile,
    allowedTypes: List<FileTypes>,
): Result<Unit> {
    if (file.isEmpty) {
        return Result.failure(BusinessLogicException("O arquivo de imagem não pode estar vazio."))
    }
    val contentType = file.contentType ?: "application/octet-stream"
    val isValidType = allowedTypes.any { it.contentType == contentType }

    if (!isValidType) {
        val fileName = file.originalFilename ?: UuidCreator.getTimeOrderedEpoch().toString()
        val fileExtension = fileName.substringAfterLast('.', "").lowercase()
        val isValidExtension = allowedTypes.any { it.name.equals(fileExtension, ignoreCase = true) }

        if (!isValidExtension) {
            val allowedContentTypes = allowedTypes.joinToString(", ") { it.contentType }
            return Result.failure(
                BusinessLogicException("Tipo de arquivo inválido. Permitidos: $allowedContentTypes. Tipo recebido: $contentType"),
            )
        }
    }
    return Result.success(Unit)
}

fun convertMultiPartToFile(
    file: MultipartFile,
    fileName: String,
): File {
    val tempDir = System.getProperty(TEMP_DIR)
    val convertedFile = File(tempDir, fileName)
    FileOutputStream(convertedFile).use { fos ->
        fos.write(file.bytes)
    }
    return convertedFile
}

inline fun <T : File, R> T.use(block: (T) -> R): R =
    try {
        block(this)
    } finally {
        if (this.exists()) {
            this.delete()
        }
    }
