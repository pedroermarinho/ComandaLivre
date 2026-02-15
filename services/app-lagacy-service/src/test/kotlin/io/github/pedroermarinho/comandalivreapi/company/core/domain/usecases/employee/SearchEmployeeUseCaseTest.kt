
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.util.factory.MockCompanyFactory
import io.github.pedroermarinho.comandalivreapi.util.factory.MockEmployeeFactory
import io.github.pedroermarinho.comandalivreapi.util.factory.MockUserFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Buscar funcionário")
class SearchEmployeeUseCaseTest {
    @MockK
    private lateinit var employeeRepository: EmployeeRepository

    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var searchUserUseCase: SearchUserUseCase

    @MockK
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @MockK
    private lateinit var employeeMapper: EmployeeMapper

    @InjectMockKs
    private lateinit var searchEmployeeUseCase: SearchEmployeeUseCase

    private lateinit var employeeEntity: EmployeeEntity
    private lateinit var employeeDTO: EmployeeDTO
    private lateinit var employeeId: UUID

    @BeforeEach
    fun setUp() {
        employeeEntity = MockEmployeeFactory.buildEmployeeEntity()
        employeeDTO = MockEmployeeFactory.buildEmployeeDTO()
        employeeId = employeeEntity.id.publicId
    }

    @Nested
    @DisplayName("Testes para o método getById")
    inner class GetByIdTests {
        @Test
        @DisplayName("Deve retornar um funcionário com sucesso pelo UUID")
        fun `get by id successfully`() {
            // Given
            every { employeeRepository.getById(employeeId) } returns Result.success(employeeEntity)
            every { searchUserUseCase.getById(employeeEntity.userId.value) } returns Result.success(MockUserFactory.build())
            every { searchCompanyUseCase.getById(employeeEntity.companyId.value) } returns Result.success(MockCompanyFactory.buildCompanyDTO())
            every { employeeMapper.toDTO(employeeEntity, any(), any()) } returns employeeDTO

            // When
            val result = searchEmployeeUseCase.getById(employeeId)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(employeeDTO, result.getOrThrow())
            verify(exactly = 1) { employeeRepository.getById(employeeId) }
            verify(exactly = 1) { employeeMapper.toDTO(employeeEntity, any(), any()) }
        }

        @Test
        @DisplayName("Deve falhar se o funcionário não for encontrado pelo UUID")
        fun `fail if employee not found by id`() {
            // Given
            val exception = NotFoundException("Funcionário não encontrado")
            every { employeeRepository.getById(employeeId) } returns Result.failure(exception)

            // When
            val result = searchEmployeeUseCase.getById(employeeId)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            verify(exactly = 1) { employeeRepository.getById(employeeId) }
            verify(exactly = 0) { employeeMapper.toDTO(any(), any(), any()) }
        }
    }
}
