package io.github.pedroermarinho.user.domain.usecases.notification

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.repositories.NotificationRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class MarkNotificationAsReadUseCase(
    private val notificationRepository: NotificationRepository,
    private val searchNotificationUseCase: SearchNotificationUseCase,
) {
    fun execute(notificationId: UUID): Result<Unit> =
        runCatching {
            val notification = searchNotificationUseCase.getEntityById(notificationId).getOrThrow()
            notificationRepository.save(notification.markAsRead()).getOrThrow()
        }
}
