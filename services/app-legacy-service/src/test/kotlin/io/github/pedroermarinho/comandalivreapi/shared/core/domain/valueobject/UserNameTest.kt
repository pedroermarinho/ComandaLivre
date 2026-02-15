package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para UserName")
class UserNameTest {
    @Test
    @DisplayName("Deve criar UserName com valor não vazio válido")
    fun shouldCreateUserNameWithValidNonBlankValue() {
        val validName = "John Doe"
        val userName = UserName(validName)
        assertEquals(validName, userName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankName = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                UserName(blankName)
            }
        assertEquals("Nome de usuário não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para nome muito curto")
    fun shouldThrowExceptionForTooShortName() {
        val shortName = "ab"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                UserName(shortName)
            }
        assertEquals("Nome de usuário deve ter no mínimo 3 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar UserName de um valor persistido")
    fun shouldRestoreUserNameFromPersistedValue() {
        val persistedValue = "Jane Smith"
        val userName = UserName.restore(persistedValue)
        assertEquals(persistedValue, userName.value)
    }
}
