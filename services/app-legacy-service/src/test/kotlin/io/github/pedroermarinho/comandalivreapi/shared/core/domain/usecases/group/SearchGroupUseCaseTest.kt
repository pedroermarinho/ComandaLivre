
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.GroupRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.GroupMapper
import io.github.pedroermarinho.comandalivreapi.util.factory.MockGroupFactory
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
@DisplayName("Caso de uso: Buscar grupo")
class SearchGroupUseCaseTest {
    @MockK
    private lateinit var groupRepository: GroupRepository

    @MockK
    private lateinit var groupMapper: GroupMapper

    @InjectMockKs
    private lateinit var searchGroupUseCase: SearchGroupUseCase

    @Test
    @DisplayName("Deve retornar um grupo com sucesso pelo UUID")
    fun `get by id successfully`() {
        // Given
        val groupId = UUID.randomUUID()
        val groupEntity = MockGroupFactory.buildGroupEntity()
        val groupDTO = MockGroupFactory.buildGroupDTO()

        every { groupRepository.getById(groupId) } returns Result.success(groupEntity)
        every { groupMapper.toDTO(groupEntity) } returns groupDTO

        // When
        val result = searchGroupUseCase.getById(groupId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(groupDTO, result.getOrThrow())
        verify(exactly = 1) { groupRepository.getById(groupId) }
        verify(exactly = 1) { groupMapper.toDTO(groupEntity) }
    }

    @Test
    @DisplayName("Deve falhar se o grupo não for encontrado pelo UUID")
    fun `fail if group not found by id`() {
        // Given
        val groupId = UUID.randomUUID()
        val exception = NotFoundException("Grupo não encontrado")
        every { groupRepository.getById(groupId) } returns Result.failure(exception)

        // When
        val result = searchGroupUseCase.getById(groupId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { groupRepository.getById(groupId) }
        verify(exactly = 0) { groupMapper.toDTO(any()) }
    }
}
