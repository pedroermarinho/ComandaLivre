package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para OrderPriority")
class OrderPriorityTest {
    @Test
    @DisplayName("Deve criar OrderPriority com valor válido entre 0 e 10")
    fun shouldCreateOrderPriorityWithValidValue() {
        val validPriority = 5
        val orderPriority = OrderPriority(validPriority)
        assertEquals(validPriority, orderPriority.value)
    }

    @Test
    @DisplayName("Deve criar OrderPriority com valor mínimo 0")
    fun shouldCreateOrderPriorityWithMinimumValue() {
        val minPriority = 0
        val orderPriority = OrderPriority(minPriority)
        assertEquals(minPriority, orderPriority.value)
    }

    @Test
    @DisplayName("Deve criar OrderPriority com valor máximo 10")
    fun shouldCreateOrderPriorityWithMaximumValue() {
        val maxPriority = 10
        val orderPriority = OrderPriority(maxPriority)
        assertEquals(maxPriority, orderPriority.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor menor que 0")
    fun shouldThrowExceptionForValueLessThanZero() {
        val invalidPriority = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                OrderPriority(invalidPriority)
            }
        assertEquals("Nível de prioridade deve ser entre 0 e 10", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor maior que 10")
    fun shouldThrowExceptionForValueGreaterThanTen() {
        val invalidPriority = 11
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                OrderPriority(invalidPriority)
            }
        assertEquals("Nível de prioridade deve ser entre 0 e 10", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar OrderPriority de um valor persistido")
    fun shouldRestoreOrderPriorityFromPersistedValue() {
        val persistedValue = 7
        val orderPriority = OrderPriority.restore(persistedValue)
        assertEquals(persistedValue, orderPriority.value)
    }
}
