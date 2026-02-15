package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para State")
class StateTest {
    @Test
    @DisplayName("Deve criar State com valor não vazio válido")
    fun shouldCreateStateWithValidNonBlankValue() {
        val validState = "SP"
        val state = State(validState)
        assertEquals(validState, state.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankState = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                State(blankState)
            }
        assertEquals("Estado não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar State de um valor persistido")
    fun shouldRestoreStateFromPersistedValue() {
        val persistedValue = "RJ"
        val state = State.restore(persistedValue)
        assertEquals(persistedValue, state.value)
    }
}
