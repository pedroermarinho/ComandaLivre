package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para DomainName")
class DomainNameTest {
    @Test
    @DisplayName("Deve criar DomainName com valor válido")
    fun shouldCreateDomainNameWithValidValue() {
        val domainName = DomainName("meu-dominio_123")
        assertEquals("meu-dominio_123", domainName.value)
    }

    @Test
    @DisplayName("Deve normalizar o valor para minúsculas e sem espaços")
    fun shouldNormalizeValueToLowerCaseAndTrim() {
        val domainName = DomainName("  MeuDominio-UPPER  ")
        assertEquals("meudominio-upper", domainName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor em branco")
    fun shouldThrowExceptionForBlankValue() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                DomainName("   ")
            }
        assertEquals("O nome de domínio não pode ficar em branco", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor muito longo")
    fun shouldThrowExceptionForTooLongValue() {
        val longValue = "a".repeat(256)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                DomainName(longValue)
            }
        assertEquals("O nome de domínio deve ter no máximo 255 caracteres", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["dominio com espaco", "dominio.com", "dominio!"])
    @DisplayName("Deve lançar BusinessLogicException para caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidValue: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                DomainName(invalidValue)
            }
        assertEquals("O nome de domínio deve conter apenas letras, números, hífens e sublinhados", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar DomainName de um valor persistido")
    fun shouldRestoreDomainNameFromPersistedValue() {
        val persistedValue = "dominio-restaurado"
        val domainName = DomainName.restore(persistedValue)
        assertEquals(persistedValue, domainName.value)
    }
}
