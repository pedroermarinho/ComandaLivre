package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class ClosingObservations private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): ClosingObservations {
            if (value.length > 2000) {
                throw BusinessLogicException("Observações de fechamento não podem exceder 2000 caracteres.")
            }
            return ClosingObservations(value)
        }

        fun restore(persistedValue: String): ClosingObservations = ClosingObservations(persistedValue)
    }
}
