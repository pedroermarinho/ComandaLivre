package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ReportText")
class ReportTextTest {
    @Test
    @DisplayName("Deve criar ReportText com comprimento válido")
    fun shouldCreateReportTextWithValidLength() {
        val validText = "This is a valid report text."
        val reportText = ReportText(validText)
        assertEquals(validText, reportText.value)
    }

    @Test
    @DisplayName("Deve criar ReportText com valor vazio")
    fun shouldCreateReportTextWithEmptyValue() {
        val emptyText = ""
        val reportText = ReportText(emptyText)
        assertEquals(emptyText, reportText.value)
    }

    @Test
    @DisplayName("Deve criar ReportText com comprimento máximo")
    fun shouldCreateReportTextWithMaxLength() {
        val maxLenText = "a".repeat(2000)
        val reportText = ReportText(maxLenText)
        assertEquals(maxLenText, reportText.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para texto excedendo 2000 caracteres")
    fun shouldThrowExceptionForTooLongText() {
        val longText = "a".repeat(2001)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ReportText(longText)
            }
        assertEquals("O texto do relatório não pode exceder 2000 caracteres.", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ReportText de um valor persistido")
    fun shouldRestoreReportTextFromPersistedValue() {
        val persistedValue = "Restored report text."
        val reportText = ReportText.restore(persistedValue)
        assertEquals(persistedValue, reportText.value)
    }
}
