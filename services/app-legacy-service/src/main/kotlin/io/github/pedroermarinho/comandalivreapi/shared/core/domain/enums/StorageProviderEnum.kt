package io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

enum class StorageProviderEnum(
    val value: String,
) {
    S3_AWS("s3_aws"),
    ;

    // from string
    companion object {
        fun fromString(value: String): StorageProviderEnum =
            entries.find { it.value == value }
                ?: throw BusinessLogicException("Serviço de armazenamento não suportado: $value")
    }
}
