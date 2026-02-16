package io.github.pedroermarinho.comandalivre.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class CommandId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): CommandId {
            if (value <= 0) {
                throw BusinessLogicException("ID da comanda deve ser um número positivo")
            }
            return CommandId(value)
        }

        fun restore(persistedValue: Int): CommandId = CommandId(persistedValue)
    }
}
