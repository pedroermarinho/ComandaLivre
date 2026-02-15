package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class ReportText private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): ReportText {
            if (value.length > 2000) {
                throw BusinessLogicException("O texto do relatório não pode exceder 2000 caracteres.")
            }
            return ReportText(value)
        }

        fun restore(persistedValue: String): ReportText = ReportText(persistedValue)
    }
}
