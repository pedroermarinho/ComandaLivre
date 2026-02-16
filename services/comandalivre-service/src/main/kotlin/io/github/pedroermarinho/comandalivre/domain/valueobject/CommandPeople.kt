package io.github.pedroermarinho.comandalivre.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class CommandPeople private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): CommandPeople {
            if (value !in 1..1000) {
                throw BusinessLogicException("Número de pessoas deve ser entre 1 e 1000")
            }
            return CommandPeople(value)
        }

        fun restore(persistedValue: Int): CommandPeople = CommandPeople(persistedValue)
    }
}
