package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class CompanyName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): CompanyName {
            val normalizedName = rawName.trim()
            if (normalizedName.isBlank()) {
                throw BusinessLogicException("O nome da empresa não pode ser vazio")
            }

            if (normalizedName.length !in 3..100) {
                throw BusinessLogicException("O nome da empresa deve conter entre 3 e 100 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.,-]{3,100}$"))) {
                throw BusinessLogicException("O nome da empresa $rawName contém caracteres inválidos")
            }

            val normalized = normalizedName.trim().replace(Regex("\\s+"), " ")
            if (normalized != normalizedName) {
                throw BusinessLogicException("O nome da empresa contém espaços inválidos")
            }

            if (!normalizedName.any { it.isLetter() }) {
                throw BusinessLogicException("O nome da empresa deve conter pelo menos uma letra")
            }
            return CompanyName(normalizedName)
        }

        fun restore(persistedValue: String): CompanyName = CompanyName(persistedValue)
    }
}
