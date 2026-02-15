package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para CommandId")
class CommandIdTest {
    @Test
    @DisplayName("Deve criar CommandId com valor positivo válido")
    fun shouldCreateCommandIdWithValidPositiveValue() {
        val validId = 1
        val commandId = CommandId(validId)
        assertEquals(validId, commandId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandId(zeroId)
            }
        assertEquals("ID da comanda deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandId(negativeId)
            }
        assertEquals("ID da comanda deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar CommandId de um valor persistido")
    fun shouldRestoreCommandIdFromPersistedValue() {
        val persistedValue = 10
        val commandId = CommandId.restore(persistedValue)
        assertEquals(persistedValue, commandId.value)
    }
}
