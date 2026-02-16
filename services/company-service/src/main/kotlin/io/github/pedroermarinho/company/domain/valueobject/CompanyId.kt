package io.github.pedroermarinho.company.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class CompanyId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): CompanyId {
            if (value <= 0) {
                throw BusinessLogicException("ID da empresa deve ser um número positivo")
            }
            return CompanyId(value)
        }

        fun restore(persistedValue: Int): CompanyId = CompanyId(persistedValue)

        fun invalid(): CompanyId = CompanyId(-1)
    }

    fun isInvalid(): Boolean = this.value < 0
}
