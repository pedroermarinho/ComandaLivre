package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ZipCode")
class ZipCodeTest {
    @Test
    @DisplayName("Deve criar ZipCode com formato válido")
    fun shouldCreateZipCodeWithValidFormat() {
        val validZipCode = "12345-678"
        val zipCode = ZipCode(validZipCode)
        assertEquals(validZipCode, zipCode.value)
    }

    @Test
    @DisplayName("Deve criar ZipCode com formato válido sem hífen")
    fun shouldCreateZipCodeWithValidFormatWithoutHyphen() {
        val validZipCode = "12345678"
        val zipCode = ZipCode(validZipCode.replace("-", ""))
        assertEquals(validZipCode, zipCode.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para formato inválido")
    fun shouldThrowExceptionForInvalidFormat() {
        val invalidZipCode = "12345"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ZipCode(invalidZipCode)
            }
        assertEquals("CEP inválido", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para CEP em branco")
    fun shouldThrowExceptionForBlankZipCode() {
        val blankZipCode = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ZipCode(blankZipCode)
            }
        assertEquals("CEP inválido", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ZipCode de um valor persistido")
    fun shouldRestoreZipCodeFromPersistedValue() {
        val persistedValue = "87654-321"
        val zipCode = ZipCode.restore(persistedValue)
        assertEquals(persistedValue, zipCode.value)
    }
}
