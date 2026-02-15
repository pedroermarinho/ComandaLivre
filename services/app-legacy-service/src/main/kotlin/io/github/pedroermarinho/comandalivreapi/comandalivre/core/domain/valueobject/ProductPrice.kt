package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import java.math.BigDecimal

@JvmInline
value class ProductPrice private constructor(
    val value: BigDecimal,
) {
    companion object {
        operator fun invoke(value: BigDecimal): ProductPrice {
            if (value < BigDecimal.ZERO) {
                throw BusinessLogicException("Preço do produto não pode ser negativo")
            }
            if (value.scale() > 2) {
                throw BusinessLogicException("Preço do produto não pode ter mais de 2 casas decimais")
            }
            return ProductPrice(value)
        }

        fun restore(persistedValue: BigDecimal): ProductPrice = ProductPrice(persistedValue)
    }
}
