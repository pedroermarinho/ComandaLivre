package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import java.math.BigDecimal

@JvmInline
value class MonetaryValue private constructor(
    val value: BigDecimal,
) {
    companion object {
        operator fun invoke(value: BigDecimal): MonetaryValue {
            if (value < BigDecimal.ZERO) {
                throw BusinessLogicException("Valor monetário não pode ser negativo")
            }
            return MonetaryValue(value)
        }

        fun restore(persistedValue: BigDecimal): MonetaryValue = MonetaryValue(persistedValue)
    }
}