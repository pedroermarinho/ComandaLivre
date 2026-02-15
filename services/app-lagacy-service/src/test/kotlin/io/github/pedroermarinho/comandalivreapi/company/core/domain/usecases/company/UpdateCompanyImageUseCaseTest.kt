
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.RegisterAssetUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import io.github.pedroermarinho.comandalivreapi.util.factory.MockCompanyFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Atualizar imagem da empresa")
class UpdateCompanyImageUseCaseTest {
    @MockK
    private lateinit var companyRepository: CompanyRepository

    @MockK
    private lateinit var registerAssetUseCase: RegisterAssetUseCase

    @MockK
    private lateinit var searchCompanyUseCase: SearchCompanyUseCase

    @MockK
    private lateinit var checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase

    @MockK
    private lateinit var createCompanyUseCase: CreateCompanyUseCase

    @InjectMockKs
    private lateinit var updateCompanyImageUseCase: UpdateCompanyImageUseCase

    private lateinit var companyId: UUID
    private lateinit var imageFile: MockMultipartFile

    @BeforeEach
    fun setUp() {
        companyId = MockConstants.COMPANY_ID_UUID
        imageFile = MockMultipartFile("image", "image.png", "image/png", "content".toByteArray())
    }

    @Nested
    @DisplayName("Testes para o método updateLogo")
    inner class UpdateLogoTests {
        @Test
        @DisplayName("Deve atualizar o logo quando as configurações já existem")
        fun `update logo when settings exist`() {
            // Given
            val assetId = EntityId(1, UUID.randomUUID())
            val settingsId = 123

            every { checkPermissionCompanyUseCase.execute(companyId) } returns Result.success(Unit)
            every { registerAssetUseCase.execute(any()) } returns Result.success(assetId)
            every { companyRepository.getById(companyId) } returns Result.success(MockCompanyFactory.buildCompanyEntity())
            justRun { companyRepository.save(any()) }

            // When
            val result = updateCompanyImageUseCase.updateLogo(companyId, imageFile)

            // Then
            assertTrue(result.isSuccess)
            verify(exactly = 0) { createCompanyUseCase.createSettings(any(), any()) }
        }

        @Test
        @DisplayName("Deve criar as configurações e atualizar o logo quando as configurações não existem")
        fun `create settings and update logo when settings do not exist`() {
            // Given
            val assetId = EntityId(1, UUID.randomUUID())
            val companyInternalId = 456

            every { checkPermissionCompanyUseCase.execute(companyId) } returns Result.success(Unit)
            every { registerAssetUseCase.execute(any()) } returns Result.success(assetId)
            every { searchCompanyUseCase.getSettingsIdByCompanyId(companyId) } returns Result.failure(NotFoundException("Settings not found"))
            every { companyRepository.getById(companyId) } returns Result.success(MockCompanyFactory.buildCompanyEntity())
            justRun { companyRepository.save(any()) }
            // When
            val result = updateCompanyImageUseCase.updateLogo(companyId, imageFile)

            // Then
            assertTrue(result.isSuccess)
            verify(exactly = 1) { companyRepository.save(any()) }
        }
    }
}
