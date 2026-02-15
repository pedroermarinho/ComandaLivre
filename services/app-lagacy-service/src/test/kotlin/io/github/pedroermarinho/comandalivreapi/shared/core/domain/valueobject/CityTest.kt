package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para City")
class CityTest {
    @Test
    @DisplayName("Deve criar City com valor não vazio válido")
    fun shouldCreateCityWithValidNonBlankValue() {
        val validCity = "São Paulo"
        val city = City(validCity)
        assertEquals(validCity, city.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankCity = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                City(blankCity)
            }
        assertEquals("Cidade não pode ser vazia", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar City de um valor persistido")
    fun shouldRestoreCityFromPersistedValue() {
        val persistedValue = "Rio de Janeiro"
        val city = City.restore(persistedValue)
        assertEquals(persistedValue, city.value)
    }
}
