
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.notification.NotificationForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.NotificationRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
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
@DisplayName("Caso de uso: Criar notificação")
class CreateNotificationUseCaseTest {
    @MockK
    private lateinit var notificationRepository: NotificationRepository

    @InjectMockKs
    private lateinit var createNotificationUseCase: CreateNotificationUseCase

    @Test
    @DisplayName("Deve criar uma notificação com sucesso")
    fun `create notification successfully`() {
        // Given
        val form =
            NotificationForm(
                eventKey = "test.event",
                title = "Test Title",
                message = "Test Message",
                userId = 1,
            )
        val notificationId = EntityId(1, UUID.randomUUID())
        every { notificationRepository.save(any()) } returns Result.success(notificationId)

        // When
        val result = createNotificationUseCase.execute(form)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(notificationId, result.getOrThrow())
        verify(exactly = 1) { notificationRepository.save(any()) }
    }

    @Test
    @DisplayName("Deve falhar ao criar uma notificação")
    fun `fail on create notification`() {
        // Given
        val form =
            NotificationForm(
                eventKey = "test.event",
                title = "Test Title",
                message = "Test Message",
                userId = 1,
            )
        val exception = RuntimeException("Database error")
        every { notificationRepository.save(any()) } returns Result.failure(exception)

        // When
        val result = createNotificationUseCase.execute(form)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { notificationRepository.save(any()) }
    }
}
