package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ProductCompanyId")
class ProductCompanyIdTest {
    @Test
    @DisplayName("Deve criar ProductCompanyId com valor positivo válido")
    fun shouldCreateProductCompanyIdWithValidPositiveValue() {
        val validId = 1
        val productCompanyId = ProductCompanyId(validId)
        assertEquals(validId, productCompanyId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductCompanyId(zeroId)
            }
        assertEquals("ID da empresa do produto deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductCompanyId(negativeId)
            }
        assertEquals("ID da empresa do produto deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProductCompanyId de um valor persistido")
    fun shouldRestoreProductCompanyIdFromPersistedValue() {
        val persistedValue = 10
        val productCompanyId = ProductCompanyId.restore(persistedValue)
        assertEquals(persistedValue, productCompanyId.value)
    }
}
