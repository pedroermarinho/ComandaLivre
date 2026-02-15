package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ProjectName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawName: String): ProjectName {
            val normalizedName = rawName.trim()
            if (normalizedName.length !in 3..100) {
                throw BusinessLogicException("O nome do projeto deve conter entre 3 e 100 caracteres")
            }

            if (!normalizedName.matches(Regex("^[a-zA-Z0-9À-ÿ '´`^~.-]{3,100}$"))) {
                throw BusinessLogicException("O nome do projeto '$normalizedName' contém caracteres inválidos")
            }

            if (!normalizedName.any { it.isLetterOrDigit() }) {
                throw BusinessLogicException("O nome do projeto deve conter pelo menos uma letra ou número")
            }
            return ProjectName(normalizedName)
        }

        fun restore(persistedValue: String): ProjectName = ProjectName(persistedValue)
    }
}
