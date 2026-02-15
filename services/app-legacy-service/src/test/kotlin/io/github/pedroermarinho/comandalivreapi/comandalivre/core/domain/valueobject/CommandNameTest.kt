package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para CommandName")
class CommandNameTest {
    @Test
    @DisplayName("Deve criar CommandName com valor válido")
    fun shouldCreateCommandNameWithValidValue() {
        val validName = "Comanda do João"
        val commandName = CommandName(validName)
        assertEquals(validName, commandName.value)
    }

    @Test
    @DisplayName("Deve normalizar o nome da comanda removendo espaços em branco no início e no fim")
    fun shouldNormalizeNameByTrimmingWhitespace() {
        val rawName = "  Comanda VIP  "
        val expectedName = "Comanda VIP"
        val commandName = CommandName(rawName)
        assertEquals(expectedName, commandName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 3 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "a"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandName(shortName)
            }
        assertEquals("O nome da comanda deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 100 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "a".repeat(101)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandName(longName)
            }
        assertEquals("O nome da comanda deve conter entre 3 e 100 caracteres", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Comanda!", "Cliente@", "Festa#"])
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidName: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandName(invalidName)
            }
        assertEquals(
            "O nome da comanda '$invalidName' contém caracteres inválidos",
            exception.message,
        )
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras ou dígitos")
    fun shouldThrowExceptionForNameWithoutLettersOrDigits() {
        val noLettersOrDigitsName = "---"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandName(noLettersOrDigitsName)
            }
        assertEquals("O nome da comanda deve conter pelo menos uma letra ou número", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar CommandName de um valor persistido")
    fun shouldRestoreCommandNameFromPersistedValue() {
        val persistedValue = "Comanda Restaurada"
        val commandName = CommandName.restore(persistedValue)
        assertEquals(persistedValue, commandName.value)
    }
}
