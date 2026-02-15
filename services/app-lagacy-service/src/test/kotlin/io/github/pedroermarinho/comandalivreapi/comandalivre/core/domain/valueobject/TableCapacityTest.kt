package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para TableCapacity")
class TableCapacityTest {
    @Test
    @DisplayName("Deve criar TableCapacity com valor válido")
    fun shouldCreateTableCapacityWithValidValue() {
        val validCapacity = 10
        val tableCapacity = TableCapacity(validCapacity)
        assertEquals(validCapacity, tableCapacity.value)
    }

    @Test
    @DisplayName("Deve criar TableCapacity com valor mínimo (1)")
    fun shouldCreateTableCapacityWithMinimumValue() {
        val validCapacity = 1
        val tableCapacity = TableCapacity(validCapacity)
        assertEquals(validCapacity, tableCapacity.value)
    }

    @Test
    @DisplayName("Deve criar TableCapacity com valor máximo (1000)")
    fun shouldCreateTableCapacityWithMaximumValue() {
        val validCapacity = 1000
        val tableCapacity = TableCapacity(validCapacity)
        assertEquals(validCapacity, tableCapacity.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroCapacity = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableCapacity(zeroCapacity)
            }
        assertEquals("Capacidade da mesa deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeCapacity = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableCapacity(negativeCapacity)
            }
        assertEquals("Capacidade da mesa deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor acima do máximo")
    fun shouldThrowExceptionForValueAboveMaximum() {
        val invalidCapacity = 1001
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableCapacity(invalidCapacity)
            }
        assertEquals("Capacidade da mesa deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar TableCapacity de um valor persistido")
    fun shouldRestoreTableCapacityFromPersistedValue() {
        val persistedValue = 5
        val tableCapacity = TableCapacity.restore(persistedValue)
        assertEquals(persistedValue, tableCapacity.value)
    }
}
