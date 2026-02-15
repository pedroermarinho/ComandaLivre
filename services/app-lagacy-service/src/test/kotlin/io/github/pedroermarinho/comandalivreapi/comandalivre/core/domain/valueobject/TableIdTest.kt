package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para TableId")
class TableIdTest {
    @Test
    @DisplayName("Deve criar TableId com valor positivo válido")
    fun shouldCreateTableIdWithValidPositiveValue() {
        val validId = 1
        val tableId = TableId(validId)
        assertEquals(validId, tableId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableId(zeroId)
            }
        assertEquals("ID da mesa deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableId(negativeId)
            }
        assertEquals("ID da mesa deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar TableId de um valor persistido")
    fun shouldRestoreTableIdFromPersistedValue() {
        val persistedValue = 10
        val tableId = TableId.restore(persistedValue)
        assertEquals(persistedValue, tableId.value)
    }
}
