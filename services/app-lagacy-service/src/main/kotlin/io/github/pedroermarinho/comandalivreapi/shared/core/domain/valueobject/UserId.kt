package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class UserId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): UserId {
            if (value <= 0) {
                throw BusinessLogicException("ID do usuário deve ser um número positivo")
            }
            return UserId(value)
        }

        fun restore(persistedValue: Int): UserId = UserId(persistedValue)
    }
}
