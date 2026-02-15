
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.factory.MockEmployeeFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Atualizar funcionário")
class UpdateEmployeeUseCaseTest {
    @MockK
    private lateinit var employeeRepository: EmployeeRepository

    @InjectMockKs
    private lateinit var updateEmployeeUseCase: UpdateEmployeeUseCase

    @Test
    @DisplayName("Deve alterar o status de um funcionário com sucesso")
    fun `change status successfully`() {
        // Given
        val employeeId = UUID.randomUUID()
        val status = false
        every { employeeRepository.getById(any<UUID>()) } returns Result.success(MockEmployeeFactory.buildEmployeeEntity())
        every { employeeRepository.save(any()) } returns
            Result.success(
                EntityId(
                    internalId = 1,
                    publicId = UUID.randomUUID(),
                ),
            )

        // When
        val result = updateEmployeeUseCase.changeStatus(employeeId, status)

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { employeeRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se o funcionário não for encontrado ao tentar alterar o status")
    fun `fail to change status if employee not found`() {
        // Given
        val employeeId = UUID.randomUUID()
        val status = false
        val exception = NotFoundException("Funcionário não encontrado")
        every { employeeRepository.getById(any<UUID>()) } returns Result.failure(exception)

        // When
        val result = updateEmployeeUseCase.changeStatus(employeeId, status)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 0) { employeeRepository.save(any()) }
    }
}
