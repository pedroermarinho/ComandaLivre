package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para Neighborhood")
class NeighborhoodTest {
    @Test
    @DisplayName("Deve criar Neighborhood com valor não vazio válido")
    fun shouldCreateNeighborhoodWithValidNonBlankValue() {
        val validNeighborhood = "Centro"
        val neighborhood = Neighborhood(validNeighborhood)
        assertEquals(validNeighborhood, neighborhood.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankNeighborhood = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                Neighborhood(blankNeighborhood)
            }
        assertEquals("Bairro não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar Neighborhood de um valor persistido")
    fun shouldRestoreNeighborhoodFromPersistedValue() {
        val persistedValue = "Bairro Restaurado"
        val neighborhood = Neighborhood.restore(persistedValue)
        assertEquals(persistedValue, neighborhood.value)
    }
}
