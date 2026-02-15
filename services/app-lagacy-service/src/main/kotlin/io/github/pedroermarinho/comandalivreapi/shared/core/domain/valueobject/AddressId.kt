package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class AddressId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): AddressId {
            if (value <= 0) {
                throw BusinessLogicException("O ID do endereço é inválido")
            }
            return AddressId(value)
        }

        fun restore(persistedValue: Int): AddressId = AddressId(persistedValue)
    }
}
