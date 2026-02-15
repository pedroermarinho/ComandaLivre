package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para OrderNotes")
class OrderNotesTest {
    @Test
    @DisplayName("Deve criar OrderNotes com comprimento válido")
    fun shouldCreateOrderNotesWithValidLength() {
        val validNotes = "This is a valid order note."
        val orderNotes = OrderNotes(validNotes)
        assertEquals(validNotes, orderNotes.value)
    }

    @Test
    @DisplayName("Deve criar OrderNotes com valor vazio")
    fun shouldCreateOrderNotesWithEmptyValue() {
        val emptyNotes = ""
        val orderNotes = OrderNotes(emptyNotes)
        assertEquals(emptyNotes, orderNotes.value)
    }

    @Test
    @DisplayName("Deve criar OrderNotes com comprimento máximo")
    fun shouldCreateOrderNotesWithMaxLength() {
        val maxLenNotes = "a".repeat(500)
        val orderNotes = OrderNotes(maxLenNotes)
        assertEquals(maxLenNotes, orderNotes.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para notas excedendo 500 caracteres")
    fun shouldThrowExceptionForTooLongNotes() {
        val longNotes = "a".repeat(501)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                OrderNotes(longNotes)
            }
        assertEquals("Observação do pedido não pode ter mais de 500 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar OrderNotes de um valor persistido")
    fun shouldRestoreOrderNotesFromPersistedValue() {
        val persistedValue = "Restored order note."
        val orderNotes = OrderNotes.restore(persistedValue)
        assertEquals(persistedValue, orderNotes.value)
    }
}
