package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para CompanyName")
class CompanyNameTest {
    @Test
    @DisplayName("Deve criar CompanyName com nome válido")
    fun shouldCreateCompanyNameWithValidName() {
        val validName = "Minha Empresa LTDA"
        val companyName = CompanyName(validName)
        assertEquals(validName, companyName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome vazio")
    fun shouldThrowExceptionForEmptyName() {
        val emptyName = ""
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyName(emptyName)
            }
        assertEquals("O nome da empresa não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 3 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "AB"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyName(shortName)
            }
        assertEquals("O nome da empresa deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 100 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "A".repeat(101)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyName(longName)
            }
        assertEquals("O nome da empresa deve conter entre 3 e 100 caracteres", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Empresa!", "Empresa@", "Empresa#"])
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidName: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyName(invalidName)
            }
        assertEquals("O nome da empresa $invalidName contém caracteres inválidos", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com espaços inválidos")
    fun shouldThrowExceptionForInvalidSpaces() {
        val invalidSpaceName = "Empresa  Teste"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyName(invalidSpaceName)
            }
        assertEquals("O nome da empresa contém espaços inválidos", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras")
    fun shouldThrowExceptionForNameWithoutLetters() {
        val noLettersName = "12345"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyName(noLettersName)
            }
        assertEquals("O nome da empresa deve conter pelo menos uma letra", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar CompanyName de um valor persistido")
    fun shouldRestoreCompanyNameFromPersistedValue() {
        val persistedValue = "Empresa Restaurada"
        val companyName = CompanyName.restore(persistedValue)
        assertEquals(persistedValue, companyName.value)
    }
}
