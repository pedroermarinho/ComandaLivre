
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
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
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Deletar usuário")
class DeleteUserUseCaseTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var searchUserUseCase: SearchUserUseCase

    @InjectMockKs
    private lateinit var deleteUserUseCase: DeleteUserUseCase

    private lateinit var userId: UUID
    private lateinit var userEntityId: EntityId

    @BeforeEach
    fun setUp() {
        userId = MockConstants.USER_ID_UUID
        userEntityId = EntityId(MockConstants.USER_ID_INT, userId)
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso (a si próprio)")
    fun `delete user successfully`() {
        // Given
        every { searchUserUseCase.getEntityById(userId) } returns Result.success(MockUserFactory.buildUserEntity())
        every { currentUserUseCase.getUserId() } returns Result.success(MockUserFactory.buildUserEntity().id.internalId)
        every { userRepository.save(any()) } returns Result.success(EntityId.createNew())

        // When
        val result = deleteUserUseCase.execute(userId)

        result.getOrThrow()

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { searchUserUseCase.getEntityById(userId) }
        verify(exactly = 1) { currentUserUseCase.getUserId() }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se o usuário tentar deletar outro usuário")
    fun `fail if user tries to delete another user`() {
        // Given
        val otherUserId = userEntityId.internalId + 1
        every { searchUserUseCase.getEntityById(userId) } returns Result.success(MockUserFactory.buildUserEntity())
        every { currentUserUseCase.getUserEntity() } returns Result.success(MockUserFactory.buildUserEntity())
        every { currentUserUseCase.getUserId() } returns Result.success(otherUserId)

        // When
        val result = deleteUserUseCase.execute(userId)
        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is BusinessLogicException)
        assertEquals("Você não tem permissão para excluir este usuário", exception?.message)
        verify(exactly = 1) { searchUserUseCase.getEntityById(userId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se o usuário a ser deletado não for encontrado")
    fun `fail if user to be deleted is not found`() {
        // Given
        val exception = NotFoundException("Usuário não encontrado")
        every { searchUserUseCase.getEntityById(userId) } returns Result.failure(exception)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { searchUserUseCase.getEntityById(userId) }
        verify(exactly = 0) { currentUserUseCase.getUserId() }
        verify(exactly = 0) { userRepository.save(any()) }
    }
}
