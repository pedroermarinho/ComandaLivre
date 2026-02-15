
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
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
@DisplayName("Caso de uso: Checar permissão na empresa")
class CheckPermissionCompanyUseCaseTest {
    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @MockK
    private lateinit var searchEmployeeUseCase: SearchEmployeeUseCase

    @InjectMockKs
    private lateinit var checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase

    @Test
    @DisplayName("Deve retornar sucesso se o usuário for funcionário")
    fun `should return success if user is employee`() {
        // Given
        val userId = MockConstants.USER_ID_INT
        val companyId = MockConstants.COMPANY_ID_INT
        every { currentUserUseCase.getUserId() } returns Result.success(userId)
        every { searchEmployeeUseCase.isEmployeeOfCompany(userId, companyId) } returns Result.success(true)

        // When
        val result = checkPermissionCompanyUseCase.execute(companyId)

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { currentUserUseCase.getUserId() }
        verify(exactly = 1) { searchEmployeeUseCase.isEmployeeOfCompany(userId, companyId) }
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for funcionário")
    fun `should fail if user is not employee`() {
        // Given
        val userId = MockConstants.USER_ID_INT
        val companyId = MockConstants.COMPANY_ID_INT
        every { currentUserUseCase.getUserId() } returns Result.success(userId)
        every { searchEmployeeUseCase.isEmployeeOfCompany(userId, companyId) } returns Result.success(false)

        // When
        val result = checkPermissionCompanyUseCase.execute(companyId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is BusinessLogicException)
        assertEquals("O usuário não tem permissão para acessar os recursos desta empresa", exception?.message)
    }
}
