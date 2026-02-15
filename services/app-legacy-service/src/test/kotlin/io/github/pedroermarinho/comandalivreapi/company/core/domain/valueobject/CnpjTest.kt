package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para CNPJ")
class CnpjTest {
    @Test
    @DisplayName("Deve criar CNPJ com valor válido e formatado")
    fun shouldCreateCnpjWithValidFormattedValue() {
        val rawCnpj = "11.222.333/0001-81"
        val expectedCnpj = "11222333000181"
        val cnpj = Cnpj(rawCnpj)
        assertEquals(expectedCnpj, cnpj.value)
    }

    @Test
    @DisplayName("Deve criar CNPJ com valor válido e sem formatação")
    fun shouldCreateCnpjWithValidUnformattedValue() {
        val rawCnpj = "11222333000181"
        val cnpj = Cnpj(rawCnpj)
        assertEquals(rawCnpj, cnpj.value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1234567890123", "123456789012345"])
    @DisplayName("Deve lançar BusinessLogicException para CNPJ com comprimento inválido")
    fun shouldThrowExceptionForInvalidLengthCnpj(invalidCnpj: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                Cnpj(invalidCnpj)
            }
        assertEquals("CNPJ deve conter 14 dígitos numéricos", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para CNPJ com todos os dígitos iguais")
    fun shouldThrowExceptionForAllEqualDigitsCnpj() {
        val invalidCnpj = "11111111111111"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                Cnpj(invalidCnpj)
            }
        assertEquals("CNPJ $invalidCnpj é inválido", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["11222333000182", "12345678000190"])
    @DisplayName("Deve lançar BusinessLogicException para CNPJ com dígitos verificadores inválidos")
    fun shouldThrowExceptionForInvalidVerifierDigits(invalidCnpj: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                Cnpj(invalidCnpj)
            }
        assertEquals("CNPJ $invalidCnpj é inválido", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar CNPJ de um valor persistido")
    fun shouldRestoreCnpjFromPersistedValue() {
        val persistedValue = "99887766000155"
        val cnpj = Cnpj.restore(persistedValue)
        assertEquals(persistedValue, cnpj.value)
    }
}
