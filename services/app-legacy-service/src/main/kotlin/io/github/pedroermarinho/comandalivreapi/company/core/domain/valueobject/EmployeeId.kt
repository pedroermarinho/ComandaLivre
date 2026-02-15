package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class EmployeeId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): EmployeeId {
            if (value <= 0) {
                throw BusinessLogicException("ID do funcionário deve ser um número positivo")
            }
            return EmployeeId(value)
        }

        fun restore(persistedValue: Int): EmployeeId = EmployeeId(persistedValue)
    }
}
