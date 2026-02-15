package io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

enum class FilePathEnum(
    val value: String,
) {
    PRODUCT_IMAGES("product-images"),
    USER_AVATARS("user-avatars"),
    RESTAURANT_LOGOS("company-logos"),
    GENERAL_DOCUMENTS("documents"),
    COMPANY_IMAGES("company-images"),
    ;

    companion object {
        fun fromString(value: String): FilePathEnum =
            entries.find { it.value == value }
                ?: throw BusinessLogicException("Caminho de arquivo não suportado: $value")
    }
}
