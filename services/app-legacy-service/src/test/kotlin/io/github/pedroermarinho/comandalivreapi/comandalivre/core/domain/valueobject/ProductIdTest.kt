package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ProductId")
class ProductIdTest {
    @Test
    @DisplayName("Deve criar ProductId com valor positivo válido")
    fun shouldCreateProductIdWithValidPositiveValue() {
        val validId = 1
        val productId = ProductId(validId)
        assertEquals(validId, productId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductId(zeroId)
            }
        assertEquals("ID do produto deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductId(negativeId)
            }
        assertEquals("ID do produto deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProductId de um valor persistido")
    fun shouldRestoreProductIdFromPersistedValue() {
        val persistedValue = 10
        val productId = ProductId.restore(persistedValue)
        assertEquals(persistedValue, productId.value)
    }
}
