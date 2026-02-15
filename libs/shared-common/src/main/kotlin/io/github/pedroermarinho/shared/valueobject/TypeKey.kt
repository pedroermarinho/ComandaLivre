package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class TypeKey private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawValue: String): TypeKey {
            val normalizedValue = rawValue.trim()
            if (normalizedValue.isBlank()) throw BusinessLogicException("A chave do tipo não pode ser vazia")
            if (normalizedValue != normalizedValue.uppercase()) throw BusinessLogicException("A chave do tipo deve ser toda em maiúsculo")
            if (!normalizedValue.matches("^[A-Z0-9_]+$".toRegex())) throw BusinessLogicException("A chave do tipo só pode conter letras maiúsculas, números e underline")
            return TypeKey(normalizedValue)
        }

        fun restore(persistedValue: String): TypeKey = TypeKey(persistedValue)
    }
}
