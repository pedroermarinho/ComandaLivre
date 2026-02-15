package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class ProductServesPersons private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): ProductServesPersons {
            if (value !in 1..1000) {
                throw BusinessLogicException("O número de pessoas que o produto serve deve ser entre 1 e 1000")
            }
            return ProductServesPersons(value)
        }

        fun restore(persistedValue: Int): ProductServesPersons = ProductServesPersons(persistedValue)
    }
}
