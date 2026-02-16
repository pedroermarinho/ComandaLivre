package io.github.pedroermarinho.user.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class State private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): State {
            if (value.isBlank()) {
                throw BusinessLogicException("Estado não pode ser vazio")
            }
            return State(value)
        }

        fun restore(persistedValue: String): State = State(persistedValue)
    }
}
