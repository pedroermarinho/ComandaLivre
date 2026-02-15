package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

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
