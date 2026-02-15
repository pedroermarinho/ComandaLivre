package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@UnitTest
@DisplayName("Teste de unidade para MonetaryValue")
class MonetaryValueTest {
    @Test
    @DisplayName("Deve criar MonetaryValue com valor positivo válido")
    fun shouldCreateMonetaryValueWithValidPositiveValue() {
        val value = BigDecimal("100.50")
        val monetaryValue = MonetaryValue(value)
        assertEquals(value, monetaryValue.value)
    }

    @Test
    @DisplayName("Deve criar MonetaryValue com valor zero")
    fun shouldCreateMonetaryValueWithZeroValue() {
        val value = BigDecimal.ZERO
        val monetaryValue = MonetaryValue(value)
        assertEquals(value, monetaryValue.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeValue = BigDecimal("-10.00")
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                MonetaryValue(negativeValue)
            }
        assertEquals("Valor monetário não pode ser negativo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar MonetaryValue de um valor persistido")
    fun shouldRestoreMonetaryValueFromPersistedValue() {
        val persistedValue = BigDecimal("250.75")
        val monetaryValue = MonetaryValue.restore(persistedValue)
        assertEquals(persistedValue, monetaryValue.value)
    }
}
