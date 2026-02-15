package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para TableName")
class TableNameTest {
    @Test
    @DisplayName("Deve criar TableName com valor válido")
    fun shouldCreateTableNameWithValidValue() {
        val validName = "Mesa 12"
        val tableName = TableName(validName)
        assertEquals(validName, tableName.value)
    }

    @Test
    @DisplayName("Deve normalizar o nome da mesa removendo espaços em branco no início e no fim")
    fun shouldNormalizeNameByTrimmingWhitespace() {
        val rawName = "  Varanda 2  "
        val expectedName = "Varanda 2"
        val tableName = TableName(rawName)
        assertEquals(expectedName, tableName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 2 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "a"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableName(shortName)
            }
        assertEquals("O nome da mesa deve conter entre 2 e 50 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 50 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "a".repeat(51)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableName(longName)
            }
        assertEquals("O nome da mesa deve conter entre 2 e 50 caracteres", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Mesa!", "Balcão@", "Área#Externa"])
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidName: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableName(invalidName)
            }
        assertEquals(
            "O nome da mesa '$invalidName' contém caracteres inválidos",
            exception.message,
        )
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras ou dígitos")
    fun shouldThrowExceptionForNameWithoutLettersOrDigits() {
        val noLettersOrDigitsName = "---"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TableName(noLettersOrDigitsName)
            }
        assertEquals("O nome da mesa deve conter pelo menos uma letra ou número", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar TableName de um valor persistido")
    fun shouldRestoreTableNameFromPersistedValue() {
        val persistedValue = "Mesa Restaurada"
        val tableName = TableName.restore(persistedValue)
        assertEquals(persistedValue, tableName.value)
    }
}
