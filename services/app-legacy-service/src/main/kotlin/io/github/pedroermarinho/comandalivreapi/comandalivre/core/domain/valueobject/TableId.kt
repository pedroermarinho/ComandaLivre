package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class TableId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): TableId {
            if (value <= 0) {
                throw BusinessLogicException("ID da mesa deve ser um número positivo")
            }
            return TableId(value)
        }

        fun restore(persistedValue: Int): TableId = TableId(persistedValue)
    }
}
