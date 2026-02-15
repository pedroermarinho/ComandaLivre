package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ProductServesPersons")
class ProductServesPersonsTest {
    @Test
    @DisplayName("Deve criar ProductServesPersons com valor positivo válido")
    fun shouldCreateProductServesPersonsWithValidPositiveValue() {
        val validPersons = 1
        val productServesPersons = ProductServesPersons(validPersons)
        assertEquals(validPersons, productServesPersons.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroPersons = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductServesPersons(zeroPersons)
            }
        assertEquals("O número de pessoas que o produto serve deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativePersons = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductServesPersons(negativePersons)
            }
        assertEquals("O número de pessoas que o produto serve deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor acima de 1000")
    fun shouldThrowExceptionForValueGreaterThan1000() {
        val excessivePersons = 1001
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductServesPersons(excessivePersons)
            }
        assertEquals("O número de pessoas que o produto serve deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProductServesPersons de um valor persistido")
    fun shouldRestoreProductServesPersonsFromPersistedValue() {
        val persistedValue = 5
        val productServesPersons = ProductServesPersons.restore(persistedValue)
        assertEquals(persistedValue, productServesPersons.value)
    }
}
