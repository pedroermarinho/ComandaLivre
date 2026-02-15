
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee.EmployeeForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.factory.MockUserFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Criar funcionário")
class CreateEmployeeUseCaseTest {
    @MockK
    private lateinit var employeeRepository: EmployeeRepository

    @MockK
    private lateinit var searchEmployeeUseCase: SearchEmployeeUseCase

    @MockK
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @InjectMockKs
    private lateinit var createEmployeeUseCase: CreateEmployeeUseCase

    private lateinit var employeeForm: EmployeeForm
    private lateinit var createdEmployeeId: EntityId

    @BeforeEach
    fun setUp() {
        employeeForm =
            EmployeeForm(
                userId = 1,
                companyId = 1,
                roleId = 1,
            )
        createdEmployeeId = EntityId(1, UUID.randomUUID())
    }

    @Test
    @DisplayName("Deve criar um funcionário com sucesso")
    fun `create employee successfully`() {
        // Given
        mockSuccessfulEmployeeCreation()

        // When
        val result = createEmployeeUseCase.create(employeeForm)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(createdEmployeeId, result.getOrThrow())
        verifySuccessfulEmployeeCreation()
    }

    @Test
    @DisplayName("Deve lançar exceção de lógica de negócio se o usuário já for funcionário")
    fun `throw BusinessLogicException if user is already an employee`() {
        // Given
        val exception = BusinessLogicException("Usuário já é um funcionário desta empresa")
        every { searchEmployeeUseCase.checkEmployeeOfCompany(employeeForm.userId, employeeForm.companyId) } returns Result.failure(exception)

        // When
        val result = createEmployeeUseCase.create(employeeForm)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { searchEmployeeUseCase.checkEmployeeOfCompany(employeeForm.userId, employeeForm.companyId) }
        verify(exactly = 0) { employeeRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se o tipo de função não for encontrado")
    fun `fail if role type not found`() {
        // Given
        val exception = NotFoundException("Role not found")
        every { searchEmployeeUseCase.checkEmployeeOfCompany(employeeForm.userId, employeeForm.companyId) } returns Result.success(Unit)
        every { searchRoleTypeUseCase.getById(employeeForm.roleId) } returns Result.failure(exception)

        // When
        val result = createEmployeeUseCase.create(employeeForm)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { searchEmployeeUseCase.checkEmployeeOfCompany(employeeForm.userId, employeeForm.companyId) }
        verify(exactly = 1) { searchRoleTypeUseCase.getById(employeeForm.roleId) }
        verify(exactly = 0) { employeeRepository.save(any()) }
    }

    private fun mockSuccessfulEmployeeCreation() {
        val roleType = MockUserFactory.buildRoleTypeValueObject()
        every { searchEmployeeUseCase.checkEmployeeOfCompany(employeeForm.userId, employeeForm.companyId) } returns Result.success(Unit)
        every { searchRoleTypeUseCase.getById(employeeForm.roleId) } returns Result.success(roleType)
        every { employeeRepository.save(any<EmployeeEntity>()) } returns Result.success(createdEmployeeId)
    }

    private fun verifySuccessfulEmployeeCreation() {
        verify(exactly = 1) { searchEmployeeUseCase.checkEmployeeOfCompany(employeeForm.userId, employeeForm.companyId) }
        verify(exactly = 1) { searchRoleTypeUseCase.getById(employeeForm.roleId) }
        verify(exactly = 1) { employeeRepository.save(any<EmployeeEntity>()) }
    }
}
