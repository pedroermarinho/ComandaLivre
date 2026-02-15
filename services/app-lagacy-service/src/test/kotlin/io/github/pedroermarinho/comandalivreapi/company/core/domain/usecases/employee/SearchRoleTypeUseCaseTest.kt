
package io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.RoleTypeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.RoleTypeMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.util.factory.MockUserFactory
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
@DisplayName("Caso de uso: Buscar tipo de função")
class SearchRoleTypeUseCaseTest {
    @MockK
    private lateinit var roleTypeRepository: RoleTypeRepository

    @MockK
    private lateinit var roleTypeMapper: RoleTypeMapper

    @InjectMockKs
    private lateinit var searchRoleTypeUseCase: SearchRoleTypeUseCase

    @Test
    @DisplayName("Deve retornar um tipo de função com sucesso pelo enum")
    fun `get by enum successfully`() {
        // Given
        val roleTypeEnum = RoleTypeEnum.RESTAURANT_OWNER
        val roleType = MockUserFactory.buildRoleTypeValueObject()
        val roleTypeDTO = MockUserFactory.buildRoleType()

        every { roleTypeRepository.getByKey(roleTypeEnum.value) } returns Result.success(roleType)
        every { roleTypeMapper.toDTO(roleType) } returns roleTypeDTO

        // When
        val result = searchRoleTypeUseCase.getByEnum(roleTypeEnum)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(roleTypeDTO, result.getOrThrow())
        verify(exactly = 1) { roleTypeRepository.getByKey(roleTypeEnum.value) }
        verify(exactly = 1) { roleTypeMapper.toDTO(roleType) }
    }

    @Test
    @DisplayName("Deve falhar se o tipo de função não for encontrado pelo enum")
    fun `fail if role type not found by enum`() {
        // Given
        val roleTypeEnum = RoleTypeEnum.RESTAURANT_OWNER
        val exception = NotFoundException("Tipo de função não encontrado")
        every { roleTypeRepository.getByKey(roleTypeEnum.value) } returns Result.failure(exception)

        // When
        val result = searchRoleTypeUseCase.getByEnum(roleTypeEnum)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { roleTypeRepository.getByKey(roleTypeEnum.value) }
        verify(exactly = 0) { roleTypeMapper.toDTO(any()) }
    }
}
