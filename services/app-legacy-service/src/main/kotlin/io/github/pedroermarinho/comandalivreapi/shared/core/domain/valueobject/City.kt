package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class City private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): City {
            if (value.isBlank()) {
                throw BusinessLogicException("Cidade não pode ser vazia")
            }
            return City(value)
        }

        fun restore(persistedValue: String): City = City(persistedValue)
    }
}
