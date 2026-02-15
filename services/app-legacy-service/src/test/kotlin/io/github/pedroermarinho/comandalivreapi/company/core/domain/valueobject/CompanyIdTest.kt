package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para CompanyId")
class CompanyIdTest {
    @Test
    @DisplayName("Deve criar CompanyId com ID válido")
    fun shouldCreateCompanyIdWithValidId() {
        val id = 123
        val companyId = CompanyId(id)

        assertEquals(id, companyId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para ID igual a zero")
    fun shouldThrowExceptionForZeroId() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyId(0)
            }
        assertEquals("ID da empresa deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para ID negativo")
    fun shouldThrowExceptionForNegativeId() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CompanyId(-1)
            }
        assertEquals("ID da empresa deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar CompanyId de um valor persistido")
    fun shouldRestoreCompanyIdFromPersistedValue() {
        val persistedValue = 456
        val companyId = CompanyId.restore(persistedValue)
        assertEquals(persistedValue, companyId.value)
    }
}
