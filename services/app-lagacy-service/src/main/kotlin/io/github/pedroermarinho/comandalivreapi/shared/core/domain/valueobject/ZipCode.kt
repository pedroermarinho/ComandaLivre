package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class ZipCode private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): ZipCode {
            if (!value.matches(Regex("^\\d{5}-?\\d{3}$"))) {
                throw BusinessLogicException("CEP inválido")
            }
            return ZipCode(value)
        }

        fun restore(persistedValue: String): ZipCode = ZipCode(persistedValue)
    }
}
