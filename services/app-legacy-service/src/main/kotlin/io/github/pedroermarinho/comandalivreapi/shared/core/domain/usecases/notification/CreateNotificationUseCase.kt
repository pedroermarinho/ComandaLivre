package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.NotificationEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.notification.NotificationForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.NotificationRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateNotificationUseCase(
    private val notificationRepository: NotificationRepository,
) {
    fun execute(form: NotificationForm): Result<EntityId> =
        runCatching {
            notificationRepository
                .save(
                    NotificationEntity.createNew(
                        publicId = form.publicId,
                        eventKey = form.eventKey,
                        title = form.title,
                        message = form.message,
                        action = form.action,
                        userId = form.userId,
                    ),
                ).getOrThrow()
        }
}
