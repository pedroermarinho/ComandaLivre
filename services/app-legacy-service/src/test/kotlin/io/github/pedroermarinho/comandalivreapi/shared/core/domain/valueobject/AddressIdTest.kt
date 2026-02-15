package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.valueobject.AddressId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para AddressId")
class AddressIdTest {
    @Test
    @DisplayName("Deve criar AddressId com valor positivo válido")
    fun shouldCreateAddressIdWithValidPositiveValue() {
        val validId = 1
        val addressId = AddressId(validId)
        assertEquals(validId, addressId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                AddressId(zeroId)
            }
        assertEquals("O ID do endereço é inválido", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                AddressId(negativeId)
            }
        assertEquals("O ID do endereço é inválido", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar AddressId de um valor persistido")
    fun shouldRestoreAddressIdFromPersistedValue() {
        val persistedValue = 10
        val addressId = AddressId.restore(persistedValue)
        assertEquals(persistedValue, addressId.value)
    }
}
