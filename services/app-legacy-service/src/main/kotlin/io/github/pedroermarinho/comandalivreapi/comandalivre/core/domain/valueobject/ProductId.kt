package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ProductId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): ProductId {
            if (value <= 0) {
                throw BusinessLogicException("ID do produto deve ser um número positivo")
            }
            return ProductId(value)
        }

        fun restore(persistedValue: Int): ProductId = ProductId(persistedValue)
    }
}
