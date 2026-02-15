package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para PhoneNumber")
class PhoneNumberTest {
    @Test
    @DisplayName("Deve criar PhoneNumber com formato válido")
    fun shouldCreatePhoneNumberWithValidFormat() {
        val validPhone = "11987654321"
        val phoneNumber = PhoneNumber(validPhone)
        assertEquals(validPhone, phoneNumber.value)
    }

    @Test
    @DisplayName("Deve criar PhoneNumber normalizando a string")
    fun shouldCreatePhoneNumberByNormalizingString() {
        val rawPhone = "(11) 98765-4321"
        val expectedPhone = "11987654321"
        val phoneNumber = PhoneNumber(rawPhone)
        assertEquals(expectedPhone, phoneNumber.value)
    }

    @Test
    @DisplayName("Deve criar PhoneNumber com 8 dígitos")
    fun shouldCreatePhoneNumberWith8Digits() {
        val phone = "98765432"
        val phoneNumber = PhoneNumber(phone)
        assertEquals(phone, phoneNumber.value)
    }

    @Test
    @DisplayName("Deve criar PhoneNumber com 10 dígitos")
    fun shouldCreatePhoneNumberWith10Digits() {
        val phone = "1187654321"
        val phoneNumber = PhoneNumber(phone)
        assertEquals(phone, phoneNumber.value)
    }

    @Test
    @DisplayName("Deve criar PhoneNumber com 11 dígitos")
    fun shouldCreatePhoneNumberWith11Digits() {
        val phone = "11987654321"
        val phoneNumber = PhoneNumber(phone)
        assertEquals(phone, phoneNumber.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para comprimento de telefone inválido")
    fun shouldThrowExceptionForInvalidLength() {
        val invalidPhone = "1234567"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                PhoneNumber(invalidPhone)
            }
        assertEquals("Comprimento do número de telefone inválido. Esperado de 8 a 11 dígitos (incluindo DDD)", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para telefone em branco")
    fun shouldThrowExceptionForBlankPhone() {
        val blankPhone = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                PhoneNumber(blankPhone)
            }
        assertEquals("O número de telefone não pode ficar em branco", exception.message)
    }

    @Test
    @DisplayName("Deve formatar corretamente um número de 11 dígitos")
    fun shouldFormat11DigitNumber() {
        val phoneNumber = PhoneNumber("11987654321")
        assertEquals("(11) 98765-4321", phoneNumber.formatted())
    }

    @Test
    @DisplayName("Deve formatar corretamente um número de 10 dígitos")
    fun shouldFormat10DigitNumber() {
        val phoneNumber = PhoneNumber("1187654321")
        assertEquals("(11) 8765-4321", phoneNumber.formatted())
    }

    @Test
    @DisplayName("Deve retornar o número original para 8 ou 9 dígitos")
    fun shouldReturnOriginalNumberFor8Or9Digits() {
        val phone8 = "87654321"
        val phoneNumber8 = PhoneNumber(phone8)
        assertEquals(phone8, phoneNumber8.formatted())
    }

    @Test
    @DisplayName("Deve restaurar PhoneNumber de um valor persistido")
    fun shouldRestorePhoneNumberFromPersistedValue() {
        val persistedValue = "21912345678"
        val phoneNumber = PhoneNumber.restore(persistedValue)
        assertEquals(persistedValue, phoneNumber.value)
    }
}
