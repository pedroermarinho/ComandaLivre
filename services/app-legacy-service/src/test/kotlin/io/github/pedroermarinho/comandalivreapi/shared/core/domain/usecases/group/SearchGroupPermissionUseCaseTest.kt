
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupPermissionRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.FeatureMapper
import io.github.pedroermarinho.comandalivreapi.util.factory.MockFeatureFactory
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
@DisplayName("Caso de uso: Buscar permissão de grupo")
class SearchGroupPermissionUseCaseTest {
    @MockK
    private lateinit var groupPermissionRepository: GroupPermissionRepository

    @MockK
    private lateinit var featureMapper: FeatureMapper

    @InjectMockKs
    private lateinit var searchGroupPermissionUseCase: SearchGroupPermissionUseCase

    private lateinit var featureEntity: FeatureEntity
    private lateinit var featureDTO: FeatureDTO
    private lateinit var groupId: UUID
    private val featureId = 1

    @BeforeEach
    fun setUp() {
        featureEntity = MockFeatureFactory.buildFeatureEntity()
        featureDTO = MockFeatureFactory.buildFeatureDTO()
        groupId = UUID.randomUUID()
    }

    @Nested
    @DisplayName("Testes para o método checkFeatureInGroup")
    inner class CheckFeatureInGroupTests {
        @Test
        @DisplayName("Deve retornar verdadeiro se a feature estiver no grupo")
        fun `should return true if feature is in group`() {
            // Given
            every { groupPermissionRepository.checkFeatureInGroup(featureId, any()) } returns true

            // When
            val result = searchGroupPermissionUseCase.checkFeatureInGroup(featureId, 1)

            // Then
            assertTrue(result)
            verify(exactly = 1) { groupPermissionRepository.checkFeatureInGroup(featureId, any()) }
        }

        @Test
        @DisplayName("Deve retornar falso se a feature não estiver no grupo")
        fun `should return false if feature is not in group`() {
            // Given
            every { groupPermissionRepository.checkFeatureInGroup(featureId, any()) } returns false

            // When
            val result = searchGroupPermissionUseCase.checkFeatureInGroup(featureId, 1)

            // Then
            assertFalse(result)
            verify(exactly = 1) { groupPermissionRepository.checkFeatureInGroup(featureId, any()) }
        }
    }

    @Nested
    @DisplayName("Testes para o método getAll")
    inner class GetAllTests {
        @Test
        @DisplayName("Deve retornar uma página de features com sucesso")
        fun `should return page of features successfully`() {
            // Given
            val pageable = PageableDTO()
            val featurePage =
                PageDTO(
                    content = listOf(featureEntity),
                    totalElements = 1,
                    totalPages = 1,
                    number = 0,
                    size = 1,
                    numberOfElements = 1,
                    hasPrevious = false,
                    hasNext = false,
                    first = true,
                    last = true,
                )
            val featureDTOPage =
                PageDTO(
                    content = listOf(featureDTO),
                    totalElements = 1,
                    totalPages = 1,
                    number = 0,
                    size = 1,
                    numberOfElements = 1,
                    hasPrevious = false,
                    hasNext = false,
                    first = true,
                    last = true,
                )

            every { groupPermissionRepository.getAll(groupId, pageable) } returns Result.success(featurePage)
            every { featureMapper.toDTO(featureEntity) } returns featureDTO

            // When
            val result = searchGroupPermissionUseCase.getAll(groupId, pageable)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(featureDTOPage, result.getOrThrow())
            verify(exactly = 1) { groupPermissionRepository.getAll(groupId, pageable) }
            verify(exactly = 1) { featureMapper.toDTO(featureEntity) }
        }
    }
}
