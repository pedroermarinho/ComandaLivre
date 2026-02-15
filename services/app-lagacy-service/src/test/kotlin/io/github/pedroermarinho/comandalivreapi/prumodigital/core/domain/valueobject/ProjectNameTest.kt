package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ProjectName")
class ProjectNameTest {
    @Test
    @DisplayName("Deve criar ProjectName com valor não vazio válido")
    fun shouldCreateProjectNameWithValidNonBlankValue() {
        val validName = "Projeto Alpha"
        val projectName = ProjectName(validName)
        assertEquals(validName, projectName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankName = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProjectName(blankName)
            }
        assertEquals("O nome do projeto deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 3 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "ab"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProjectName(shortName)
            }
        assertEquals("O nome do projeto deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 100 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "a".repeat(101)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProjectName(longName)
            }
        assertEquals("O nome do projeto deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters() {
        val invalidName = "Projeto!"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProjectName(invalidName)
            }
        assertEquals(
            "O nome do projeto 'Projeto!' contém caracteres inválidos",
            exception.message,
        )
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras ou números")
    fun shouldThrowExceptionForNameWithoutLettersOrDigits() {
        val noLettersOrDigitsName = "---..."
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProjectName(noLettersOrDigitsName)
            }
        assertEquals("O nome do projeto deve conter pelo menos uma letra ou número", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProjectName de um valor persistido")
    fun shouldRestoreProjectNameFromPersistedValue() {
        val persistedValue = "Projeto Restaurado"
        val projectName = ProjectName.restore(persistedValue)
        assertEquals(persistedValue, projectName.value)
    }
}
