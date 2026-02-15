
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.UpdateUserForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.asset.RegisterAssetUseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.factory.MockUserFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Atualizar usuário")
class UpdateUserUseCaseTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var searchUserUseCase: SearchUserUseCase

    @MockK
    private lateinit var registerAssetUseCase: RegisterAssetUseCase

    @InjectMockKs
    private lateinit var updateUserUseCase: UpdateUserUseCase

    private lateinit var updateUserForm: UpdateUserForm
    private lateinit var currentUser: UserDTO

    @BeforeEach
    fun setUp() {
        updateUserForm =
            UpdateUserForm(
                name = "New Test User",
            )
        currentUser = MockUserFactory.build()
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    fun `update user successfully`() {
        // Given
        every { currentUserUseCase.getUserEntity() } returns Result.success(MockUserFactory.buildUserEntity())
        every { userRepository.save(any()) } returns Result.success(EntityId.createNew())

        // When
        val result = updateUserUseCase.execute(updateUserForm)

        // Then
        assertTrue(result.isSuccess)
        verifyOrder {
            currentUserUseCase.getUserEntity()
            userRepository.save(any())
        }
    }

    @Test
    @DisplayName("Deve falhar se o usuário atual não for encontrado")
    fun `fail if current user not found`() {
        // Given
        val exception = BusinessLogicException("Usuário não encontrado")
        every { currentUserUseCase.getUserEntity() } returns Result.failure(exception)

        // When
        val result = updateUserUseCase.execute(updateUserForm)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { currentUserUseCase.getUserEntity() }
        verify(exactly = 0) { userRepository.save(any()) }
    }
}
