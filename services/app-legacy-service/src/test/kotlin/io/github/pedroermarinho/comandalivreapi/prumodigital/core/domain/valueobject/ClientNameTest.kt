package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para ClientName")
class ClientNameTest {
    @Test
    @DisplayName("Deve criar ClientName com valor válido")
    fun shouldCreateClientNameWithValidValue() {
        val validName = "Cliente Teste"
        val clientName = ClientName(validName)
        assertEquals(validName, clientName.value)
    }

    @Test
    @DisplayName("Deve normalizar o nome do cliente removendo espaços em branco no início e no fim")
    fun shouldNormalizeNameByTrimmingWhitespace() {
        val rawName = "  Cliente VIP  "
        val expectedName = "Cliente VIP"
        val clientName = ClientName(rawName)
        assertEquals(expectedName, clientName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 3 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "ab"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ClientName(shortName)
            }
        assertEquals("O nome do cliente deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 100 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "a".repeat(101)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ClientName(longName)
            }
        assertEquals("O nome do cliente deve conter entre 3 e 100 caracteres", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Cliente!", "Cliente@", "Cliente#"])
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidName: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ClientName(invalidName)
            }
        assertEquals(
            "O nome do cliente '$invalidName' contém caracteres inválidos",
            exception.message,
        )
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras")
    fun shouldThrowExceptionForNameWithoutLetters() {
        val noLettersName = "12345"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ClientName(noLettersName)
            }
        assertEquals("O nome do cliente deve conter pelo menos uma letra", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ClientName de um valor persistido")
    fun shouldRestoreClientNameFromPersistedValue() {
        val persistedValue = "Cliente Restaurado"
        val clientName = ClientName.restore(persistedValue)
        assertEquals(persistedValue, clientName.value)
    }
}
