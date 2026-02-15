package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class OrderNotes private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): OrderNotes {
            if (value.length > 500) {
                throw BusinessLogicException("Observação do pedido não pode ter mais de 500 caracteres")
            }
            return OrderNotes(value)
        }

        fun restore(persistedValue: String): OrderNotes = OrderNotes(persistedValue)
    }
}
