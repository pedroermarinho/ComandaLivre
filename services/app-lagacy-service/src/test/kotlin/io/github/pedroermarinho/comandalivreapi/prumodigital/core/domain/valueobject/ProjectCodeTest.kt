package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para ProjectCode")
class ProjectCodeTest {
    @Test
    @DisplayName("Deve criar ProjectCode com valor não vazio válido")
    fun shouldCreateProjectCodeWithValidNonBlankValue() {
        val validCode = "PROJ-001"
        val projectCode = ProjectCode(validCode)
        assertEquals(validCode, projectCode.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor vazio")
    fun shouldThrowExceptionForBlankValue() {
        val blankCode = "   "
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                ProjectCode(blankCode)
            }
        assertEquals("Código do projeto não pode ser vazio", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar ProjectCode de um valor persistido")
    fun shouldRestoreProjectCodeFromPersistedValue() {
        val persistedValue = "PROJ-RESTORED"
        val projectCode = ProjectCode.restore(persistedValue)
        assertEquals(persistedValue, projectCode.value)
    }
}
