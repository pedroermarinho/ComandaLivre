
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.NotificationRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.NotificationMapper
import io.github.pedroermarinho.comandalivreapi.util.factory.MockNotificationFactory
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
@DisplayName("Caso de uso: Buscar notificação")
class SearchNotificationUseCaseTest {
    @MockK
    private lateinit var notificationRepository: NotificationRepository

    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var notificationMapper: NotificationMapper

    @InjectMockKs
    private lateinit var searchNotificationUseCase: SearchNotificationUseCase

    @Test
    @DisplayName("Deve buscar todas as notificações com sucesso")
    fun `get all notifications successfully`() {
        // Given
        val user = MockUserFactory.build()
        val pageable = PageableDTO()
        val notificationEntity = MockNotificationFactory.buildNotificationEntity()
        val notificationPage =
            PageDTO(
                content = listOf(notificationEntity),
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
        val notificationDTO = MockNotificationFactory.buildNotificationDTO()
        val notificationDTOPage = notificationPage.map { notificationDTO }

        every { currentUserUseCase.getUser() } returns Result.success(user)
        every { notificationRepository.getByUserId(user.id.internalId, pageable) } returns Result.success(notificationPage)
        every { notificationMapper.toDTO(notificationEntity, null) } returns notificationDTO

        // When
        val result = searchNotificationUseCase.getAll(pageable)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(notificationDTOPage, result.getOrThrow())
        verify(exactly = 1) { currentUserUseCase.getUser() }
        verify(exactly = 1) { notificationRepository.getByUserId(user.id.internalId, pageable) }
    }
}
