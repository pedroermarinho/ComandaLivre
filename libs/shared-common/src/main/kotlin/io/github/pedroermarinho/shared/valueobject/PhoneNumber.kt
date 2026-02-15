package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class PhoneNumber private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawPhone: String): PhoneNumber {
            val normalizedPhone = rawPhone.replace(Regex("[^0-9]"), "")

            if (normalizedPhone.isBlank()) {
                throw BusinessLogicException("O número de telefone não pode ficar em branco")
            }

            if (normalizedPhone.length !in 8..11) {
                throw BusinessLogicException("Comprimento do número de telefone inválido. Esperado de 8 a 11 dígitos (incluindo DDD)")
            }

            return PhoneNumber(normalizedPhone)
        }

        fun restore(persistedValue: String): PhoneNumber = PhoneNumber(persistedValue)
    }

    fun formatted(): String =
        when (value.length) {
            11 -> "(${value.take(2)}) ${value.substring(2, 7)}-${value.substring(7)}"
            10 -> "(${value.take(2)}) ${value.substring(2, 6)}-${value.substring(6)}"
            else -> value
        }
}