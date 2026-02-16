package io.github.pedroermarinho.prumodigital.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ProjectCode private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): ProjectCode {
            if (value.isBlank()) {
                throw BusinessLogicException("Código do projeto não pode ser vazio")
            }
            return ProjectCode(value)
        }

        fun restore(persistedValue: String): ProjectCode = ProjectCode(persistedValue)
    }
}
