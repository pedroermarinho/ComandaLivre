package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ClosingObservations")
class ClosingObservationsTest {
    @Test
    @DisplayName("Deve criar ClosingObservations com comprimento válido")
    fun shouldCreateClosingObservationsWithValidLength() {
        val validObservation = "This is a valid observation."
        val closingObservations = ClosingObservations(validObservation)
        assertEquals(validObservation, closingObservations.value)
    }

    @Test
    @DisplayName("Deve criar ClosingObservations com valor vazio")
    fun shouldCreateClosingObservationsWithEmptyValue() {
        val emptyObservation = ""
        val closingObservations = ClosingObservations(emptyObservation)
        assertEquals(emptyObservation, closingObservations.value)
    }

    @Test
    @DisplayName("Deve criar ClosingObservations com comprimento máximo")
    fun shouldCreateClosingObservationsWithMaxLength() {
        val maxLenObservation = "a".repeat(2000)
        val closingObservations = ClosingObservations(maxLenObservation)
        assertEquals(maxLenObservation, closingObservations.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para observações excedendo 2000 caracteres")
    fun shouldThrowExceptionForTooLongObservations() {
        val longObservation = "a".repeat(2001)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ClosingObservations(longObservation)
            }
        assertEquals("Observações de fechamento não podem exceder 2000 caracteres.", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ClosingObservations de um valor persistido")
    fun shouldRestoreClosingObservationsFromPersistedValue() {
        val persistedValue = "Restored observation."
        val closingObservations = ClosingObservations.restore(persistedValue)
        assertEquals(persistedValue, closingObservations.value)
    }
}
