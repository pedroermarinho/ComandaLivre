package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class TypeName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawValue: String): TypeName {
            val normalizedValue = rawValue.trim()
            if (normalizedValue.isBlank()) throw BusinessLogicException("O nome do tipo não pode ser vazia")
            return TypeName(normalizedValue)
        }

        fun restore(persistedValue: String): TypeName = TypeName(persistedValue)
    }
}
