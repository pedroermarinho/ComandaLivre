
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInviteMapper
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.comandalivreapi.util.factory.MockEmployeeInviteFactory
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
@DisplayName("Caso de uso: Buscar convite de funcionário")
class SearchEmployeeInviteUseCaseTest {
    @MockK
    private lateinit var employeeInviteRepository: EmployeeInviteRepository

    @MockK
    private lateinit var currentUserService: CurrentUserService

    @MockK
    private lateinit var searchUserUseCase: SearchUserUseCase

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @MockK
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @MockK
    private lateinit var searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase

    @MockK
    private lateinit var employeeInviteMapper: EmployeeInviteMapper

    @InjectMockKs
    private lateinit var searchEmployeeInviteUseCase: SearchEmployeeInviteUseCase

    private lateinit var inviteEntity: EmployeeInviteEntity
    private lateinit var inviteDTO: EmployeeInviteDTO
    private lateinit var inviteId: UUID

    @BeforeEach
    fun setUp() {
        inviteEntity = MockEmployeeInviteFactory.buildEmployeeInviteEntity()
        inviteDTO = MockEmployeeInviteFactory.buildEmployeeInviteDTO()
        inviteId = inviteEntity.id.publicId
    }

    @Test
    @DisplayName("Deve retornar um convite com sucesso pelo UUID")
    fun `get by id successfully`() {
        // Given
        every { employeeInviteRepository.getById(inviteId) } returns Result.success(inviteEntity)
        every { employeeInviteMapper.toDTO(inviteEntity) } returns Result.success(inviteDTO)

        // When
        val result = searchEmployeeInviteUseCase.getById(inviteId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(inviteDTO, result.getOrThrow())
        verify(exactly = 1) { employeeInviteRepository.getById(inviteId) }
        verify(exactly = 1) { employeeInviteMapper.toDTO(inviteEntity) }
    }

    @Test
    @DisplayName("Deve falhar se o convite não for encontrado pelo UUID")
    fun `fail if invite not found by id`() {
        // Given
        val exception = NotFoundException("Convite não encontrado")
        every { employeeInviteRepository.getById(inviteId) } returns Result.failure(exception)

        // When
        val result = searchEmployeeInviteUseCase.getById(inviteId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { employeeInviteRepository.getById(inviteId) }
        verify(exactly = 0) { employeeInviteMapper.toDTO(any()) }
    }
}
