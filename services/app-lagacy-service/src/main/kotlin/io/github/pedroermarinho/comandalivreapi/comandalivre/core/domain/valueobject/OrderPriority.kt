package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class OrderPriority private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): OrderPriority {
            if (value !in 0..10) {
                throw BusinessLogicException("Nível de prioridade deve ser entre 0 e 10")
            }
            return OrderPriority(value)
        }

        fun restore(persistedValue: Int): OrderPriority = OrderPriority(persistedValue)
    }
}
