package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class EmailAddress private constructor(
    val value: String,
) {
    companion object {
        private const val MAX_LENGTH = 254
        private val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()

        operator fun invoke(rawEmail: String): EmailAddress {
            val normalizedEmail = rawEmail.lowercase().trim()

            if (normalizedEmail.isBlank()) {
                throw BusinessLogicException("O endereço de e-mail não pode ficar em branco")
            }

            if (!EMAIL_REGEX.matches(normalizedEmail)) {
                throw BusinessLogicException("Formato de endereço de e-mail inválido: $rawEmail")
            }
            if (normalizedEmail.length > MAX_LENGTH) {
                throw BusinessLogicException("O endereço de e-mail deve ter menos de 255 caracteres")
            }

            return EmailAddress(normalizedEmail)
        }

        fun restore(persistedValue: String): EmailAddress {
            val restoredValue = persistedValue.lowercase().trim()
            return EmailAddress(restoredValue)
        }
    }

    override fun toString(): String = value
}