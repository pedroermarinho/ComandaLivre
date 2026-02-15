
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.group

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserGroupRepository
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
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
@DisplayName("Caso de uso: Buscar grupo de usuário")
class SearchUserGroupUseCaseTest {
    @MockK
    private lateinit var userGroupRepository: UserGroupRepository

    @InjectMockKs
    private lateinit var searchUserGroupUseCase: SearchUserGroupUseCase

    @Test
    @DisplayName("Deve retornar as chaves de feature de um usuário com sucesso")
    fun `get feature keys by user id successfully`() {
        // Given
        val userId = MockConstants.USER_ID_INT
        val featureKeys = listOf("feature1", "feature2")
        every { userGroupRepository.getFeatureKeysByUserId(userId) } returns Result.success(featureKeys)

        // When
        val result = searchUserGroupUseCase.getFeatureKeysByUserId(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(featureKeys, result.getOrThrow())
        verify(exactly = 1) { userGroupRepository.getFeatureKeysByUserId(userId) }
    }

    @Test
    @DisplayName("Deve falhar ao buscar as chaves de feature")
    fun `fail on get feature keys`() {
        // Given
        val userId = MockConstants.USER_ID_INT
        val exception = RuntimeException("Database error")
        every { userGroupRepository.getFeatureKeysByUserId(userId) } returns Result.failure(exception)

        // When
        val result = searchUserGroupUseCase.getFeatureKeysByUserId(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { userGroupRepository.getFeatureKeysByUserId(userId) }
    }
}
