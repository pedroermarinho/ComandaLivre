package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee.EmployeeForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.util.factory.MockEmployeeInviteFactory
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
@DisplayName("Caso de uso: Alterar status do convite de funcionário")
class ChangeStatusEmployeeInviteUseCaseTest {
    @MockK
    private lateinit var employeeInviteRepository: EmployeeInviteRepository

    @MockK
    private lateinit var searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase

    @MockK
    private lateinit var currentUserService: CurrentUserService

    @MockK
    private lateinit var createEmployeeUseCase: CreateEmployeeUseCase

    @MockK
    private lateinit var searchUserUseCase: SearchUserUseCase

    @MockK
    private lateinit var searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase

    @InjectMockKs
    private lateinit var changeStatusEmployeeInviteUseCase: ChangeStatusEmployeeInviteUseCase

    private lateinit var inviteId: UUID

    @BeforeEach
    fun setUp() {
        inviteId = UUID.randomUUID()
    }

    @Test
    @DisplayName("Deve aceitar um convite e criar um funcionário com sucesso")
    fun `accept invite and create employee successfully`() {
        // Given
        val invite = MockEmployeeInviteFactory.buildEmployeeInviteDTO()
        val user = MockUserFactory.buildUserAuthDTO()
        val status = MockEmployeeInviteFactory.buildEmployeeInviteStatus()
        every { searchEmployeeInviteUseCase.getById(inviteId) } returns Result.success(invite)
        every { currentUserService.getLoggedUser() } returns Result.success(user)
        every { searchEmployeeInviteStatusUseCase.getByEnum(EmployeeInviteEnum.ACCEPTED) } returns Result.success(status)
        every { employeeInviteRepository.save(any()) } returns Result.success(invite.id)
        every { employeeInviteRepository.getById(any<UUID>()) } returns Result.success(MockEmployeeInviteFactory.buildEmployeeInviteEntity())
        every { createEmployeeUseCase.create(any<EmployeeForm>()) } returns Result.success(invite.id)

        // When
        val result = changeStatusEmployeeInviteUseCase.changeStatus(inviteId, EmployeeInviteEnum.ACCEPTED)
        result.getOrThrow()
        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { createEmployeeUseCase.create(any<EmployeeForm>()) }
    }

    @Test
    @DisplayName("Deve recusar um convite com sucesso sem criar um funcionário")
    fun `decline invite successfully without creating employee`() {
        // Given
        val invite = MockEmployeeInviteFactory.buildEmployeeInviteDTO()
        val user = MockUserFactory.buildUserAuthDTO()
        val status = MockEmployeeInviteFactory.buildEmployeeInviteStatus()
        every { searchEmployeeInviteUseCase.getById(inviteId) } returns Result.success(invite)
        every { currentUserService.getLoggedUser() } returns Result.success(user)
        every { searchEmployeeInviteStatusUseCase.getByEnum(EmployeeInviteEnum.REJECTED) } returns Result.success(status)
        every { employeeInviteRepository.getById(any<UUID>()) } returns Result.success(MockEmployeeInviteFactory.buildEmployeeInviteEntity())
        every { employeeInviteRepository.save(any()) } returns Result.success(invite.id)

        // When
        val result = changeStatusEmployeeInviteUseCase.changeStatus(inviteId, EmployeeInviteEnum.REJECTED)

        result.getOrThrow()
        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 0) { createEmployeeUseCase.create(any<EmployeeForm>()) }
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for o destinatário do convite")
    fun `fail if user is not the invite recipient`() {
        // Given
        val invite = MockEmployeeInviteFactory.buildEmployeeInviteDTO()
        val otherUser = MockUserFactory.buildUserAuthDTO().copy(email = "other@email.com")
        every { searchEmployeeInviteUseCase.getById(inviteId) } returns Result.success(invite)
        every { currentUserService.getLoggedUser() } returns Result.success(otherUser)
        every { employeeInviteRepository.getById(any<UUID>()) } returns Result.success(MockEmployeeInviteFactory.buildEmployeeInviteEntity())
        every { employeeInviteRepository.save(any()) } returns Result.success(invite.id)

        // When
        val result = changeStatusEmployeeInviteUseCase.changeStatus(inviteId, EmployeeInviteEnum.ACCEPTED)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is BusinessLogicException)
        assertEquals("Usuário não autorizado a alterar este EmployeeInvite", exception?.message)
        verify(exactly = 0) { createEmployeeUseCase.create(any<EmployeeForm>()) }
    }
}
