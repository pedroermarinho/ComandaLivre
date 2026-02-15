package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ProductName")
class ProductNameTest {
    @Test
    @DisplayName("Deve criar ProductName com valor válido")
    fun shouldCreateProductNameWithValidValue() {
        val validName = "Pizza Margherita"
        val productName = ProductName(validName)
        assertEquals(validName, productName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 3 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "Pi"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductName(shortName)
            }
        assertEquals("O nome do produto deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 100 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "a".repeat(101)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductName(longName)
            }
        assertEquals("O nome do produto deve conter entre 3 e 100 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters() {
        val invalidName = "Pizza!"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductName(invalidName)
            }
        assertEquals("O nome do produto Pizza! contém caracteres inválidos", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras")
    fun shouldThrowExceptionForNameWithoutLetters() {
        val noLettersName = "12345"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductName(noLettersName)
            }
        assertEquals("O nome do produto deve conter pelo menos uma letra", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProductName de um valor persistido")
    fun shouldRestoreProductNameFromPersistedValue() {
        val persistedValue = "Hamburguer Clássico"
        val productName = ProductName.restore(persistedValue)
        assertEquals(persistedValue, productName.value)
    }
}
