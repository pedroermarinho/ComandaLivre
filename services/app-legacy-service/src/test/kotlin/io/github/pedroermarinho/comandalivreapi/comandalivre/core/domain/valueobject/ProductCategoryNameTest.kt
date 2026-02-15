package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para ProductCategoryName")
class ProductCategoryNameTest {
    @Test
    @DisplayName("Deve criar ProductCategoryName com valor válido")
    fun shouldCreateProductCategoryNameWithValidValue() {
        val validName = "Bebidas"
        val productCategoryName = ProductCategoryName(validName)
        assertEquals(validName, productCategoryName.value)
    }

    @Test
    @DisplayName("Deve normalizar o nome removendo espaços em branco no início e no fim")
    fun shouldNormalizeNameByTrimmingWhitespace() {
        val rawName = "  Sobremesas  "
        val expectedName = "Sobremesas"
        val productCategoryName = ProductCategoryName(rawName)
        assertEquals(expectedName, productCategoryName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com menos de 3 caracteres")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "a"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductCategoryName(shortName)
            }
        assertEquals("O nome da categoria do produto deve conter entre 3 e 50 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome com mais de 50 caracteres")
    fun shouldThrowExceptionForTooLongName() {
        val longName = "a".repeat(51)
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductCategoryName(longName)
            }
        assertEquals("O nome da categoria do produto deve conter entre 3 e 50 caracteres", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Bebidas!", "Sucos@", "Comidas#"])
    @DisplayName("Deve lançar BusinessLogicException para nome com caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidName: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductCategoryName(invalidName)
            }
        assertEquals(
            "O nome da categoria do produto '$invalidName' contém caracteres inválidos",
            exception.message,
        )
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome sem letras")
    fun shouldThrowExceptionForNameWithoutLetters() {
        val noLettersName = "12345"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProductCategoryName(noLettersName)
            }
        assertEquals("O nome da categoria do produto deve conter pelo menos uma letra", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProductCategoryName de um valor persistido")
    fun shouldRestoreProductCategoryNameFromPersistedValue() {
        val persistedValue = "Comidas"
        val productCategoryName = ProductCategoryName.restore(persistedValue)
        assertEquals(persistedValue, productCategoryName.value)
    }
}
