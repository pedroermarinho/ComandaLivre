package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class DomainName private constructor(
    val value: String,
) {
    companion object {
        private const val MAX_LENGTH = 255
        private val DOMAIN_PATTERN = "^[a-z0-9_-]+$".toRegex()

        operator fun invoke(rawValue: String): DomainName {
            val normalizedValue = rawValue.lowercase().trim()
            if (normalizedValue.isBlank()) {
                throw BusinessLogicException("O nome de domínio não pode ficar em branco")
            }

            if (normalizedValue.length > MAX_LENGTH) {
                throw BusinessLogicException("O nome de domínio deve ter no máximo $MAX_LENGTH caracteres")
            }

            if (!DOMAIN_PATTERN.matches(normalizedValue)) {
                throw BusinessLogicException("O nome de domínio deve conter apenas letras, números, hífens e sublinhados")
            }
            return DomainName(normalizedValue)
        }

        fun restore(persistedValue: String): DomainName = DomainName(persistedValue)
    }

    override fun toString(): String = value
}
