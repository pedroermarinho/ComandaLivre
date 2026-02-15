package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class TableName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): TableName {
            val normalizedName = rawName.trim()
            if (normalizedName.length !in 2..50) {
                throw BusinessLogicException("O nome da mesa deve conter entre 2 e 50 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.-]{2,50}$"))) {
                throw BusinessLogicException("O nome da mesa '$normalizedName' contém caracteres inválidos")
            }

            if (!normalizedName.any { it.isLetterOrDigit() }) {
                throw BusinessLogicException("O nome da mesa deve conter pelo menos uma letra ou número")
            }
            return TableName(normalizedName)
        }

        fun restore(persistedValue: String): TableName = TableName(persistedValue)
    }
}
