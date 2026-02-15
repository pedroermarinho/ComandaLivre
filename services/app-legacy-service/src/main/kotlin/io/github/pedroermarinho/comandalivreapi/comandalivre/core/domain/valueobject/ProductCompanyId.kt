package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ProductCompanyId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): ProductCompanyId {
            if (value <= 0) {
                throw BusinessLogicException("ID da empresa do produto deve ser um número positivo")
            }
            return ProductCompanyId(value)
        }

        fun restore(persistedValue: Int): ProductCompanyId = ProductCompanyId(persistedValue)
    }
}
