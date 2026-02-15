
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.EmployeeInviteEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteStatusRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInviteStatusMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.util.factory.MockEmployeeInviteFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Buscar status de convite de funcionário")
class SearchEmployeeInviteStatusUseCaseTest {
    @MockK
    private lateinit var employeeInviteStatusRepository: EmployeeInviteStatusRepository

    @MockK
    private lateinit var employeeInviteStatusMapper: EmployeeInviteStatusMapper

    @InjectMockKs
    private lateinit var searchEmployeeInviteStatusUseCase: SearchEmployeeInviteStatusUseCase

    @Test
    @DisplayName("Deve retornar um status com sucesso pelo enum")
    fun `get by enum successfully`() {
        // Given
        val statusEnum = EmployeeInviteEnum.PENDING
        val statusEntity = MockEmployeeInviteFactory.buildEmployeeInviteStatus()

        every { employeeInviteStatusRepository.getByKey(statusEnum.value) } returns Result.success(statusEntity)

        // When
        val result = searchEmployeeInviteStatusUseCase.getByEnum(statusEnum)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(statusEntity, result.getOrThrow())
        verify(exactly = 1) { employeeInviteStatusRepository.getByKey(statusEnum.value) }
    }

    @Test
    @DisplayName("Deve falhar se o status não for encontrado pelo enum")
    fun `fail if status not found by enum`() {
        // Given
        val statusEnum = EmployeeInviteEnum.PENDING
        val exception = NotFoundException("Status não encontrado")
        every { employeeInviteStatusRepository.getByKey(statusEnum.value) } returns Result.failure(exception)

        // When
        val result = searchEmployeeInviteStatusUseCase.getByEnum(statusEnum)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { employeeInviteStatusRepository.getByKey(statusEnum.value) }
    }
}
