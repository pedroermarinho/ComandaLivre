package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para EmployeeId")
class EmployeeIdTest {
    @Test
    @DisplayName("Deve criar EmployeeId com ID válido")
    fun shouldCreateEmployeeIdWithValidId() {
        val id = 1
        val employeeId = EmployeeId(id)
        assertEquals(id, employeeId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para ID igual a zero")
    fun shouldThrowExceptionForZeroId() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                EmployeeId(0)
            }
        assertEquals("ID do funcionário deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para ID negativo")
    fun shouldThrowExceptionForNegativeId() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                EmployeeId(-1)
            }
        assertEquals("ID do funcionário deve ser um número positivo", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar EmployeeId de um valor persistido")
    fun shouldRestoreEmployeeIdFromPersistedValue() {
        val persistedValue = 10
        val employeeId = EmployeeId.restore(persistedValue)
        assertEquals(persistedValue, employeeId.value)
    }
}
