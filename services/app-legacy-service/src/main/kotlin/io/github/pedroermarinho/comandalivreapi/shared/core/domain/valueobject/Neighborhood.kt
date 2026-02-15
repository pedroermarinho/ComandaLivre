package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class Neighborhood private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): Neighborhood {
            if (value.isBlank()) {
                throw BusinessLogicException("Bairro não pode ser vazio")
            }
            return Neighborhood(value)
        }

        fun restore(persistedValue: String): Neighborhood = Neighborhood(persistedValue)
    }
}
