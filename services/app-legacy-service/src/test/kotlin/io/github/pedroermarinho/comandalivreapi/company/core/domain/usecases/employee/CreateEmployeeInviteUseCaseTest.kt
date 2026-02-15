
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.employee.EmployeeInviteRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.EmployeeInviteCreatedEvent
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import io.github.pedroermarinho.comandalivreapi.util.factory.MockCompanyFactory
import io.github.pedroermarinho.comandalivreapi.util.factory.MockEmployeeInviteFactory
import io.github.pedroermarinho.comandalivreapi.util.factory.MockUserFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Criar convite de funcionário")
class CreateEmployeeInviteUseCaseTest {
    @MockK
    private lateinit var employeeInviteRepository: EmployeeInviteRepository

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @MockK
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @MockK
    private lateinit var searchUserUseCase: SearchUserUseCase

    @MockK
    private lateinit var searchEmployeeUseCase: SearchEmployeeUseCase

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase

    @MockK
    private lateinit var searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase

    @InjectMockKs
    private lateinit var createEmployeeInviteUseCase: CreateEmployeeInviteUseCase

    private lateinit var inviteRequest: EmployeeInviteRequest

    @BeforeEach
    fun setUp() {
        inviteRequest =
            EmployeeInviteRequest(
                email = MockConstants.USER_EMAIL,
                companyId = MockConstants.COMPANY_ID_UUID,
                roleId = MockConstants.ROLE_TYPE_ID_UUID,
            )
    }

    @Test
    @DisplayName("Deve criar um convite com sucesso")
    fun `create invite successfully`() {
        // Given
        val company = MockCompanyFactory.buildCompanyDTO()
        val roleType = MockUserFactory.buildRoleTypeValueObject()
        val user = MockUserFactory.build()
        val status = MockEmployeeInviteFactory.buildEmployeeInviteStatus()
        val createdInviteId = EntityId(1, UUID.randomUUID())
        val createdInvite = MockEmployeeInviteFactory.buildEmployeeInviteDTO()

        every { searchCompanyUseCase.getById(inviteRequest.companyId) } returns Result.success(company)
        every { searchRoleTypeUseCase.getById(inviteRequest.roleId) } returns Result.success(roleType)
        every { searchUserUseCase.getByEmail(inviteRequest.email) } returns Result.success(user)
        every { searchEmployeeInviteStatusUseCase.getByEnum(any()) } returns Result.success(status)
        every { searchEmployeeUseCase.checkEmployeeOfCompany(user.id.internalId, company.id.internalId) } returns Result.success(Unit)
        every { employeeInviteRepository.save(any<EmployeeInviteEntity>()) } returns Result.success(createdInviteId)
        every { searchEmployeeInviteUseCase.getById(createdInviteId.internalId) } returns Result.success(createdInvite)
        justRun { eventPublisher.publishEvent(any<EmployeeInviteCreatedEvent>()) }

        // When
        val result = createEmployeeInviteUseCase.create(inviteRequest)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(createdInviteId, result.getOrThrow())
        verify(exactly = 1) { employeeInviteRepository.save(any<EmployeeInviteEntity>()) }
        verify(exactly = 1) { eventPublisher.publishEvent(any<EmployeeInviteCreatedEvent>()) }
    }

    @Test
    @DisplayName("Deve falhar se a empresa não for encontrada")
    fun `fail if company not found`() {
        // Given
        val exception = NotFoundException("Empresa não encontrada")
        every { searchCompanyUseCase.getById(inviteRequest.companyId) } returns Result.failure(exception)

        // When
        val result = createEmployeeInviteUseCase.create(inviteRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 0) { employeeInviteRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se a função não for encontrada")
    fun `fail if role not found`() {
        // Given
        val company = MockCompanyFactory.buildCompanyDTO()
        val exception = NotFoundException("Função não encontrada")
        every { searchCompanyUseCase.getById(inviteRequest.companyId) } returns Result.success(company)
        every { searchRoleTypeUseCase.getById(inviteRequest.roleId) } returns Result.failure(exception)

        // When
        val result = createEmployeeInviteUseCase.create(inviteRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 0) { employeeInviteRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se o usuário já for um funcionário")
    fun `fail if user is already an employee`() {
        // Given
        val company = MockCompanyFactory.buildCompanyDTO()
        val roleType = MockUserFactory.buildRoleTypeValueObject()
        val user = MockUserFactory.build()
        val status = MockEmployeeInviteFactory.buildEmployeeInviteStatus()
        val exception = BusinessLogicException("Usuário já é funcionário do restaurante")

        every { searchCompanyUseCase.getById(inviteRequest.companyId) } returns Result.success(company)
        every { searchRoleTypeUseCase.getById(inviteRequest.roleId) } returns Result.success(roleType)
        every { searchUserUseCase.getByEmail(inviteRequest.email) } returns Result.success(user)
        every { searchEmployeeInviteStatusUseCase.getByEnum(any()) } returns Result.success(status)
        every { searchEmployeeUseCase.checkEmployeeOfCompany(user.id.internalId, company.id.internalId) } returns Result.failure(exception)

        // When
        val result = createEmployeeInviteUseCase.create(inviteRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 0) { employeeInviteRepository.save(any()) }
    }
}
