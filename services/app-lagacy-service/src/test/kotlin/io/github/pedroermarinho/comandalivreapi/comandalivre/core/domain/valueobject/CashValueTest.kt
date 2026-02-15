package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@UnitTest
@DisplayName("Teste de unidade para CashValue")
class CashValueTest {
    @Test
    @DisplayName("Deve criar CashValue com valor positivo válido")
    fun shouldCreateCashValueWithValidPositiveValue() {
        val value = BigDecimal("100.50")
        val cashValue = CashValue(value)
        assertEquals(value, cashValue.value)
    }

    @Test
    @DisplayName("Deve criar CashValue com valor zero")
    fun shouldCreateCashValueWithZeroValue() {
        val value = BigDecimal.ZERO
        val cashValue = CashValue(value)
        assertEquals(value, cashValue.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeValue = BigDecimal("-10.00")
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CashValue(negativeValue)
            }
        assertEquals("Valor em caixa não pode ser negativo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor com mais de 2 casas decimais")
    fun shouldThrowExceptionForMoreThanTwoDecimalPlaces() {
        val invalidValue = BigDecimal("100.123")
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CashValue(invalidValue)
            }
        assertEquals("Valor em caixa não pode ter mais de 2 casas decimais", exception.message)
    }

    @Test
    @DisplayName("Deve criar CashValue com 1 casa decimal")
    fun shouldCreateCashValueWithOneDecimalPlace() {
        val value = BigDecimal("150.5")
        val cashValue = CashValue(value)
        assertEquals(value, cashValue.value)
    }

    @Test
    @DisplayName("Deve restaurar CashValue de um valor persistido")
    fun shouldRestoreCashValueFromPersistedValue() {
        val persistedValue = BigDecimal("250.75")
        val cashValue = CashValue.restore(persistedValue)
        assertEquals(persistedValue, cashValue.value)
    }
}
