package io.github.pedroermarinho.user.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class Street private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): Street {
            if (value.isBlank()) {
                throw BusinessLogicException("Rua não pode ser vazia")
            }
            return Street(value)
        }

        fun restore(persistedValue: String): Street = Street(persistedValue)
    }
}
