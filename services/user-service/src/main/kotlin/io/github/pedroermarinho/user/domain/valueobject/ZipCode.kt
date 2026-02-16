package io.github.pedroermarinho.user.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

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
