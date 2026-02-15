package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@UnitTest
@DisplayName("Teste de unidade para TypeKey")
class TypeKeyTest {
    @Test
    @DisplayName("Deve criar TypeKey com valor válido")
    fun shouldCreateTypeKeyWithValidValue() {
        val typeKey = TypeKey("VALID_KEY_1")
        assertEquals("VALID_KEY_1", typeKey.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor em branco")
    fun shouldThrowExceptionForBlankValue() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TypeKey("   ")
            }
        assertEquals("A chave do tipo não pode ser vazia", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor com minúsculas")
    fun shouldThrowExceptionForLowercaseValue() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TypeKey("invalid_key")
            }
        assertEquals("A chave do tipo deve ser toda em maiúsculo", exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["INVALID-KEY", "INVALID KEY", "INVALID!"])
    @DisplayName("Deve lançar BusinessLogicException para caracteres inválidos")
    fun shouldThrowExceptionForInvalidCharacters(invalidValue: String) {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TypeKey(invalidValue)
            }
        assertEquals("A chave do tipo só pode conter letras maiúsculas, números e underline", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar TypeKey de um valor persistido")
    fun shouldRestoreTypeKeyFromPersistedValue() {
        val persistedValue = "PERSISTED_KEY"
        val typeKey = TypeKey.restore(persistedValue)
        assertEquals(persistedValue, typeKey.value)
    }
}
