package io.github.pedroermarinho.comandalivre.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class TableCapacity private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): TableCapacity {
            if (value !in 1..1000) {
                throw BusinessLogicException("Capacidade da mesa deve ser entre 1 e 1000")
            }
            return TableCapacity(value)
        }

        fun restore(persistedValue: Int): TableCapacity = TableCapacity(persistedValue)
    }
}
