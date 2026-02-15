
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
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
@DisplayName("Caso de uso: Buscar empresa")
class SearchCompanyUseCaseTest {
    @MockK
    private lateinit var companyRepository: CompanyRepository

    @MockK
    private lateinit var companyMapper: CompanyMapper

    @InjectMockKs
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    private lateinit var companyEntity: CompanyEntity
    private lateinit var companyDTO: CompanyDTO
    private lateinit var companyId: UUID

    @BeforeEach
    fun setUp() {
        companyEntity = MockCompanyFactory.buildCompanyEntity()
        companyDTO = MockCompanyFactory.buildCompanyDTO()
        companyId = companyEntity.id.publicId
    }

    @Nested
    @DisplayName("Testes para o método getById")
    inner class GetByIdTests {
        @Test
        @DisplayName("Deve retornar uma empresa com sucesso pelo UUID")
        fun `get by id successfully`() {
            // Given
            every { companyRepository.getById(companyId) } returns Result.success(companyEntity)
            every { companyMapper.toDTO(companyEntity) } returns Result.success(companyDTO)

            // When
            val result = searchCompanyUseCase.getById(companyId)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(companyDTO, result.getOrThrow())
            verify(exactly = 1) { companyRepository.getById(companyId) }
            verify(exactly = 1) { companyMapper.toDTO(companyEntity) }
        }

        @Test
        @DisplayName("Deve falhar se a empresa não for encontrada pelo UUID")
        fun `fail if company not found by id`() {
            // Given
            val exception = NotFoundException("Empresa não encontrada")
            every { companyRepository.getById(companyId) } returns Result.failure(exception)

            // When
            val result = searchCompanyUseCase.getById(companyId)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            verify(exactly = 1) { companyRepository.getById(companyId) }
            verify(exactly = 0) { companyMapper.toDTO(any()) }
        }
    }

    @Nested
    @DisplayName("Testes para o método getAll")
    inner class GetAllTests {
        @Test
        @DisplayName("Deve retornar uma página de empresas com sucesso")
        fun `get all successfully`() {
            // Given
            val pageable = PageableDTO()
            val companyPage =
                PageDTO(
                    content = listOf(companyEntity),
                    totalPages = 1,
                    totalElements = 1,
                    number = 0,
                    size = 1,
                    numberOfElements = 1,
                    hasPrevious = false,
                    hasNext = false,
                    first = true,
                    last = true,
                )
            val companyDTOPage =
                PageDTO(
                    content = listOf(companyDTO),
                    totalPages = 1,
                    totalElements = 1,
                    number = 0,
                    size = 1,
                    numberOfElements = 1,
                    hasPrevious = false,
                    hasNext = false,
                    first = true,
                    last = true,
                )

            every { companyRepository.getAll(pageable) } returns Result.success(companyPage)
            every { companyMapper.toDTO(companyEntity) } returns Result.success(companyDTO)

            // When
            val result = searchCompanyUseCase.getAll(pageable)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(companyDTOPage, result.getOrThrow())
            verify(exactly = 1) { companyRepository.getAll(pageable) }
            verify(exactly = 1) { companyMapper.toDTO(companyEntity) }
        }
    }
}
