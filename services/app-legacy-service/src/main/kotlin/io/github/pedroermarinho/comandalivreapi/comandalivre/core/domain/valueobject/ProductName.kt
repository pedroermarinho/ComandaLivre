package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ProductName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): ProductName {
            val normalizedName = rawName.trim()
            if (normalizedName.length !in 3..100) {
                throw BusinessLogicException("O nome do produto deve conter entre 3 e 100 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.,-]{3,100}$"))) {
                throw BusinessLogicException("O nome do produto $rawName contém caracteres inválidos")
            }

            if (!normalizedName.any { it.isLetter() }) {
                throw BusinessLogicException("O nome do produto deve conter pelo menos uma letra")
            }
            return ProductName(normalizedName)
        }

        fun restore(persistedValue: String): ProductName = ProductName(persistedValue)
    }
}
