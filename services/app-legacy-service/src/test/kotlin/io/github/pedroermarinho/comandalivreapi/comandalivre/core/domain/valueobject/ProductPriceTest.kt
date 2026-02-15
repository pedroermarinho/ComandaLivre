package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@UnitTest
@DisplayName("Teste de unidade para ProductPrice")
class ProductPriceTest {
    @Test
    @DisplayName("Deve criar ProductPrice com valor positivo válido")
    fun shouldCreateProductPriceWithValidPositiveValue() {
        val value = BigDecimal("10.50")
        val productPrice = ProductPrice(value)
        assertEquals(value, productPrice.value)
    }

    @Test
    @DisplayName("Deve criar ProductPrice com valor zero")
    fun shouldCreateProductPriceWithZeroValue() {
        val value = BigDecimal.ZERO
        val productPrice = ProductPrice(value)
        assertEquals(value, productPrice.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeValue = BigDecimal("-1.00")
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductPrice(negativeValue)
            }
        assertEquals("Preço do produto não pode ser negativo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor com mais de 2 casas decimais")
    fun shouldThrowExceptionForMoreThanTwoDecimalPlaces() {
        val invalidValue = BigDecimal("10.123")
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductPrice(invalidValue)
            }
        assertEquals("Preço do produto não pode ter mais de 2 casas decimais", exception.message)
    }

    @Test
    @DisplayName("Deve criar ProductPrice com 1 casa decimal")
    fun shouldCreateProductPriceWithOneDecimalPlace() {
        val value = BigDecimal("15.5")
        val productPrice = ProductPrice(value)
        assertEquals(value, productPrice.value)
    }

    @Test
    @DisplayName("Deve restaurar ProductPrice de um valor persistido")
    fun shouldRestoreProductPriceFromPersistedValue() {
        val persistedValue = BigDecimal("25.75")
        val productPrice = ProductPrice.restore(persistedValue)
        assertEquals(persistedValue, productPrice.value)
    }
}
