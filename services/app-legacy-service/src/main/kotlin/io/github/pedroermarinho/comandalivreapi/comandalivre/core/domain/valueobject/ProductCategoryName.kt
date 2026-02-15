package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ProductCategoryName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): ProductCategoryName {
            val normalizedName = rawName.trim()
            if (normalizedName.length !in 3..50) {
                throw BusinessLogicException("O nome da categoria do produto deve conter entre 3 e 50 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.-]{3,50}$"))) {
                throw BusinessLogicException("O nome da categoria do produto '$normalizedName' contém caracteres inválidos")
            }

            if (!normalizedName.any { it.isLetter() }) {
                throw BusinessLogicException("O nome da categoria do produto deve conter pelo menos uma letra")
            }
            return ProductCategoryName(normalizedName)
        }

        fun restore(persistedValue: String): ProductCategoryName = ProductCategoryName(persistedValue)
    }
}
