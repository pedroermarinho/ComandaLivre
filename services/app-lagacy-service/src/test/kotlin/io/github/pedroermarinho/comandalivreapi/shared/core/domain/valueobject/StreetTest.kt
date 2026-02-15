package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para Street")
class StreetTest {
    @Test
    @DisplayName("Deve criar Street com valor não vazio válido")
    fun shouldCreateStreetWithValidNonBlankValue() {
        val validStreet = "Rua Principal"
        val street = Street(validStreet)
        assertEquals(validStreet, street.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankStreet = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                Street(blankStreet)
            }
        assertEquals("Rua não pode ser vazia", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar Street de um valor persistido")
    fun shouldRestoreStreetFromPersistedValue() {
        val persistedValue = "Avenida Restaurada"
        val street = Street.restore(persistedValue)
        assertEquals(persistedValue, street.value)
    }
}
