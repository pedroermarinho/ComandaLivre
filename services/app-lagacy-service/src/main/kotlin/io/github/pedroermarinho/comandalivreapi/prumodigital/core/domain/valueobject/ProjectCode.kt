package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

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
