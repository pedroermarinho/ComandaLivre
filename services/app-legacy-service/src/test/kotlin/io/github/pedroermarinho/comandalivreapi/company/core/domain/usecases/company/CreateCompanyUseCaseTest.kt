package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.forms.employee.EmployeeForm
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.request.company.CompanyCreateRequest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.CreateEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import io.github.pedroermarinho.comandalivreapi.util.factory.MockCompanyFactory
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
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Criar empresa")
class CreateCompanyUseCaseTest {
    @MockK
    private lateinit var companyRepository: CompanyRepository

    @MockK
    private lateinit var createEmployeeUseCase: CreateEmployeeUseCase

    @MockK
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var searchTypeCompanyUseCase: SearchTypeCompanyUseCase

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @InjectMockKs
    private lateinit var createCompanyUseCase: CreateCompanyUseCase

    private lateinit var companyCreateRequest: CompanyCreateRequest
    private lateinit var companyType: CompanyType
    private lateinit var createdCompanyId: EntityId

    @BeforeEach
    fun setUp() {
        companyCreateRequest = MockCompanyFactory.buildCompanyCreateRequest()
        companyType = MockCompanyFactory.buildCompanyType()
        createdCompanyId = EntityId(MockConstants.COMPANY_ID_INT, UUID.randomUUID())
    }

    @Test
    @DisplayName("Deve criar uma empresa com sucesso")
    fun `create company successfully`() {
        // Given
        mockSuccessfulCompanyCreation()

        // When
        val result = createCompanyUseCase.create(companyCreateRequest)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(createdCompanyId, result.getOrThrow())
        verifySuccessfulCompanyCreation()
    }

    @Test
    @DisplayName("Deve lançar exceção de lógica de negócio se o tipo de empresa não for suportado")
    fun `throw BusinessLogicException if company type is not supported`() {
        // Given
        val unsupportedCompanyType =
            MockCompanyFactory.buildCompanyType(
                key = "UNSUPPORTED_TYPE",
                name = "Unsupported Type",
            )
        every { searchTypeCompanyUseCase.getByEnum(any()) } returns Result.success(unsupportedCompanyType)

        // When
        val result = createCompanyUseCase.create(companyCreateRequest)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is BusinessLogicException)
        assertEquals("Tipo de empresa não suportado", exception?.message)

        verify(exactly = 1) { searchTypeCompanyUseCase.getByEnum(any()) }
        verifyNoCompanyCreationSideEffects()
    }

    @Test
    @DisplayName("Deve falhar se o tipo de empresa não for encontrado")
    fun `fail if company type not found`() {
        // Given
        val exception = NotFoundException("Company type not found")
        every { searchTypeCompanyUseCase.getByEnum(any()) } returns Result.failure(exception)

        // When
        val result = createCompanyUseCase.create(companyCreateRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        verify(exactly = 1) { searchTypeCompanyUseCase.getByEnum(any()) }
        verifyNoCompanyCreationSideEffects()
    }

    @Test
    @DisplayName("Deve criar as configurações da empresa com sucesso")
    fun `create company settings successfully`() {
        // Given
        val companySettingsForm = MockCompanyFactory.buildCompanySettingsForm()
        val companyId = MockConstants.COMPANY_ID_INT
        val settingsId = EntityId(1, UUID.randomUUID())

        val company = MockCompanyFactory.buildCompanyEntity().copy(id = EntityId(companyId, UUID.randomUUID()))
        every { companyRepository.getById(companyId) } returns Result.success(company)
        every { companyRepository.save(any<CompanyEntity>()) } returns Result.success(settingsId)

        // When
        val result = createCompanyUseCase.createSettings(companyId, companySettingsForm)

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { companyRepository.save(any<CompanyEntity>()) }
    }

    private fun mockSuccessfulCompanyCreation() {
        val user = MockUserFactory.build()
        val roleType = MockUserFactory.buildRoleType()
        val companyDTO = MockCompanyFactory.buildCompanyDTO()
        val employeeId = EntityId(1, UUID.randomUUID())

        every { searchTypeCompanyUseCase.getByEnum(any()) } returns Result.success(companyType)
        every { companyRepository.save(any<CompanyEntity>()) } returns Result.success(createdCompanyId)
        every { searchRoleTypeUseCase.getByEnum(RoleTypeEnum.RESTAURANT_OWNER) } returns Result.success(roleType)
        every { currentUserUseCase.getUser() } returns Result.success(user)
        every { createEmployeeUseCase.create(any<EmployeeForm>()) } returns Result.success(employeeId)
        every { searchCompanyUseCase.getById(createdCompanyId.internalId) } returns Result.success(companyDTO)
        every { eventPublisher.publishEvent(any<CompanyCreatedEvent>()) } answers { nothing }
    }

    private fun verifySuccessfulCompanyCreation() {
        verify(exactly = 1) { companyRepository.save(any<CompanyEntity>()) }
        verify(exactly = 1) { searchRoleTypeUseCase.getByEnum(RoleTypeEnum.RESTAURANT_OWNER) }
        verify(exactly = 1) { currentUserUseCase.getUser() }
        verify(exactly = 1) { createEmployeeUseCase.create(any<EmployeeForm>()) }
        verify(exactly = 1) { searchCompanyUseCase.getById(createdCompanyId.internalId) }
        verify(exactly = 1) { eventPublisher.publishEvent(any<CompanyCreatedEvent>()) }
    }

    private fun verifyNoCompanyCreationSideEffects() {
        verify(exactly = 0) { companyRepository.save(any()) }
        verify(exactly = 0) { createEmployeeUseCase.create(any()) }
        verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        verify(exactly = 0) { companyRepository.save(any<CompanyEntity>()) }
    }
}
