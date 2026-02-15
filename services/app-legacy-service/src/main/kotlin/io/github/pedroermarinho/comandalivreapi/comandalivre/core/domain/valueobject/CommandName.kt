package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class CommandName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): CommandName {
            val normalizedName = rawName.trim()
            if (normalizedName.length !in 3..100) {
                throw BusinessLogicException("O nome da comanda deve conter entre 3 e 100 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.-]{3,100}$"))) {
                throw BusinessLogicException("O nome da comanda '$normalizedName' contém caracteres inválidos")
            }

            if (!normalizedName.any { it.isLetterOrDigit() }) {
                throw BusinessLogicException("O nome da comanda deve conter pelo menos uma letra ou número")
            }
            return CommandName(normalizedName)
        }

        fun restore(persistedValue: String): CommandName = CommandName(persistedValue)
    }
}
