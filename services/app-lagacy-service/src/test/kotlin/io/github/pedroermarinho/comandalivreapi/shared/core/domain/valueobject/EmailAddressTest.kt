package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para EmailAddress")
class EmailAddressTest {
    @Test
    @DisplayName("Deve criar EmailAddress com formato de e-mail válido")
    fun shouldCreateEmailAddressWithValidFormat() {
        val validEmail = "test@example.com"
        val emailAddress = EmailAddress(validEmail)
        assertEquals(validEmail, emailAddress.value)
    }

    @Test
    @DisplayName("Deve normalizar o e-mail para minúsculas e sem espaços")
    fun shouldNormalizeEmail() {
        val rawEmail = "  Test@Example.COM  "
        val expectedEmail = "test@example.com"
        val emailAddress = EmailAddress(rawEmail)
        assertEquals(expectedEmail, emailAddress.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para formato de e-mail inválido")
    fun shouldThrowExceptionForInvalidFormat() {
        val invalidEmail = "invalid-email"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                EmailAddress(invalidEmail)
            }
        assertEquals("Formato de endereço de e-mail inválido: $invalidEmail", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para e-mail em branco")
    fun shouldThrowExceptionForBlankEmail() {
        val blankEmail = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                EmailAddress(blankEmail)
            }
        assertEquals("O endereço de e-mail não pode ficar em branco", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para e-mail muito longo")
    fun shouldThrowExceptionForTooLongEmail() {
        val longEmail = "a".repeat(255) + "@example.com"
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                EmailAddress(longEmail)
            }
        assertEquals("O endereço de e-mail deve ter menos de 255 caracteres", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar EmailAddress de um valor persistido")
    fun shouldRestoreEmailAddressFromPersistedValue() {
        val persistedValue = "persisted@example.com"
        val emailAddress = EmailAddress.restore(persistedValue)
        assertEquals(persistedValue, emailAddress.value)
    }
}
