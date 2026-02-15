
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.UserEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group.AddUserToGroupUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group.SearchUserGroupUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.factory.MockUserFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Criar usuário")
class CreateUserUseCaseTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var addUserToGroupUseCase: AddUserToGroupUseCase

    @MockK
    private lateinit var searchUserGroupUseCase: SearchUserGroupUseCase

    @InjectMockKs
    private lateinit var createUserUseCase: CreateUserUseCase

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    fun `create user successfully`() {
        // Given
        val userAuth = MockUserFactory.buildUserAuthDTO()
        val userId = EntityId(1, UUID.randomUUID())
        val userEntity = MockUserFactory.buildUserEntity()

        every { userRepository.save(any<UserEntity>()) } returns Result.success(userId)
        every { addUserToGroupUseCase.execute(any(), any()) } returns Result.success(Unit)
        every { userRepository.getById(userId.internalId) } returns Result.success(userEntity)
        justRun { eventPublisher.publishEvent(any<NewUserRegisteredEvent>()) }

        // When
        val result = createUserUseCase.execute(userAuth)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userId, result.getOrThrow())
        verify(exactly = 1) { userRepository.save(any<UserEntity>()) }
        verify(exactly = 1) { addUserToGroupUseCase.execute(any(), any()) }
        verify(exactly = 1) { eventPublisher.publishEvent(any<NewUserRegisteredEvent>()) }
    }
}
