package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para UserId")
class UserIdTest {
    @Test
    @DisplayName("Deve criar UserId com valor positivo válido")
    fun shouldCreateUserIdWithValidPositiveValue() {
        val validId = 1
        val userId = UserId(validId)
        assertEquals(validId, userId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                UserId(zeroId)
            }
        assertEquals("ID do usuário deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                UserId(negativeId)
            }
        assertEquals("ID do usuário deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar UserId de um valor persistido")
    fun shouldRestoreUserIdFromPersistedValue() {
        val persistedValue = 10
        val userId = UserId.restore(persistedValue)
        assertEquals(persistedValue, userId.value)
    }
}
