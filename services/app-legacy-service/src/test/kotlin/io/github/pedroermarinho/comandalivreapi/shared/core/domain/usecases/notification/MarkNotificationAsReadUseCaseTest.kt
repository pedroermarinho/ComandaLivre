
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.NotificationRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.factory.MockNotificationFactory
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
@DisplayName("Caso de uso: Marcar notificação como lida")
class MarkNotificationAsReadUseCaseTest {
    @MockK
    private lateinit var notificationRepository: NotificationRepository

    @MockK
    private lateinit var searchNotificationUseCase: SearchNotificationUseCase

    @InjectMockKs
    private lateinit var markNotificationAsReadUseCase: MarkNotificationAsReadUseCase

    @Test
    @DisplayName("Deve marcar uma notificação como lida com sucesso")
    fun `mark notification as read successfully`() {
        // Given
        val notificationId = UUID.randomUUID()
        every { notificationRepository.save(any()) } returns Result.success(EntityId.createNew())
        every { searchNotificationUseCase.getEntityById(any()) } returns Result.success(MockNotificationFactory.buildNotificationEntity())

        // When
        val result = markNotificationAsReadUseCase.execute(notificationId)

        result.getOrThrow()

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { notificationRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar se a notificação não for encontrada")
    fun `fail if notification not found`() {
        // Given
        val notificationId = UUID.randomUUID()
        val exception = NotFoundException("Notificação não encontrada")
        every { searchNotificationUseCase.getEntityById(any()) } returns Result.failure(exception)
        every { notificationRepository.save(any()) }

        // When
        val result = markNotificationAsReadUseCase.execute(notificationId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { searchNotificationUseCase.getEntityById(any()) }
        verify(exactly = 0) { notificationRepository.save(any()) }
    }
}
