package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import java.math.BigDecimal

@JvmInline
value class CashValue private constructor(
    val value: BigDecimal,
) {
    companion object {
        operator fun invoke(value: BigDecimal): CashValue {
            if (value < BigDecimal.ZERO) {
                throw BusinessLogicException("Valor em caixa não pode ser negativo")
            }
            if (value.scale() > 2) {
                throw BusinessLogicException("Valor em caixa não pode ter mais de 2 casas decimais")
            }
            return CashValue(value)
        }

        fun restore(persistedValue: BigDecimal): CashValue = CashValue(persistedValue)
    }
}
