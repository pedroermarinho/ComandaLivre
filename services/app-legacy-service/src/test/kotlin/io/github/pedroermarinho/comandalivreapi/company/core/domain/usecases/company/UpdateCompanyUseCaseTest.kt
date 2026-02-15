
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyUpdateRequest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.CreateAddressUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.UpdateAddressUseCase
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import io.github.pedroermarinho.comandalivreapi.util.factory.MockCompanyFactory
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
@DisplayName("Caso de uso: Atualizar empresa")
class UpdateCompanyUseCaseTest {
    @MockK
    private lateinit var companyRepository: CompanyRepository

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @MockK
    private lateinit var updateAddressUseCase: UpdateAddressUseCase

    @MockK
    private lateinit var createAddressUseCase: CreateAddressUseCase

    @MockK
    private lateinit var createCompanyUseCase: CreateCompanyUseCase

    @MockK
    private lateinit var checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase

    @InjectMockKs
    private lateinit var updateCompanyUseCase: UpdateCompanyUseCase

    private lateinit var companyUpdateRequest: CompanyUpdateRequest
    private lateinit var companyEntity: CompanyEntity
    private lateinit var companyId: UUID

    @BeforeEach
    fun setUp() {
        companyId = MockConstants.COMPANY_ID_UUID
        companyUpdateRequest =
            CompanyUpdateRequest(
                name = "New Test Company",
                email = "newtest@company.com",
                phone = "11888888888",
                cnpj = "29.952.180/0001-93",
                description = "A new test company for unit tests",
                isPublic = false,
            )
        companyEntity = MockCompanyFactory.buildCompanyEntity()
    }

    @Nested
    @DisplayName("Testes para o método update")
    inner class UpdateTests {
        @Test
        @DisplayName("Deve atualizar uma empresa com sucesso")
        fun `update company successfully`() {
            // Given
            mockSuccessfulUpdate()

            // When
            val result = updateCompanyUseCase.update(companyId, companyUpdateRequest)

            // Then
            assertTrue(result.isSuccess)
            verifySuccessfulUpdate()
        }

        @Test
        @DisplayName("Deve falhar se o usuário não tiver permissão")
        fun `fail if user does not have permission`() {
            // Given
            val exception = BusinessLogicException("O usuário não tem permissão para acessar os recursos desta empresa")
            every { checkPermissionCompanyUseCase.execute(companyId) } returns Result.failure(exception)

            // When
            val result = updateCompanyUseCase.update(companyId, companyUpdateRequest)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            verify(exactly = 1) { checkPermissionCompanyUseCase.execute(companyId) }
            verify(exactly = 0) { companyRepository.getById(any<UUID>()) }
            verify(exactly = 0) { companyRepository.save(any()) }
        }

        @Test
        @DisplayName("Deve falhar se a empresa não for encontrada")
        fun `fail if company not found`() {
            // Given
            val exception = NotFoundException("Empresa não encontrada")
            every { checkPermissionCompanyUseCase.execute(companyId) } returns Result.success(Unit)
            every { companyRepository.getById(companyId) } returns Result.failure(exception)

            // When
            val result = updateCompanyUseCase.update(companyId, companyUpdateRequest)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            verify(exactly = 1) { checkPermissionCompanyUseCase.execute(companyId) }
            verify(exactly = 1) { companyRepository.getById(companyId) }
            verify(exactly = 0) { companyRepository.save(any()) }
        }
    }

    private fun mockSuccessfulUpdate() {
        every { checkPermissionCompanyUseCase.execute(companyId) } returns Result.success(Unit)
        every { companyRepository.getById(companyId) } returns Result.success(companyEntity)
        every { companyRepository.save(any<CompanyEntity>()) } returns Result.success(companyEntity.id)
    }

    private fun verifySuccessfulUpdate() {
        verify(exactly = 1) { checkPermissionCompanyUseCase.execute(companyId) }
        verify(exactly = 1) { companyRepository.getById(companyId) }
        verify(exactly = 1) { companyRepository.save(any<CompanyEntity>()) }
    }
}
