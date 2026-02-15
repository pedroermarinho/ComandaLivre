package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ClientName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): ClientName {
            val normalizedName = rawName.trim()
            if (normalizedName.length !in 3..100) {
                throw BusinessLogicException("O nome do cliente deve conter entre 3 e 100 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.-]{3,100}$"))) {
                throw BusinessLogicException("O nome do cliente '$normalizedName' contém caracteres inválidos")
            }

            if (!normalizedName.any { it.isLetter() }) {
                throw BusinessLogicException("O nome do cliente deve conter pelo menos uma letra")
            }
            return ClientName(normalizedName)
        }

        fun restore(persistedValue: String): ClientName = ClientName(persistedValue)
    }
}
