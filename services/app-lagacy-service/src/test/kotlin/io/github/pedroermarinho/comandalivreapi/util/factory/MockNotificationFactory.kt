
package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.notification.NotificationDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.NotificationEntity
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import java.time.LocalDateTime

object MockNotificationFactory {
    fun buildNotificationEntity(): NotificationEntity =
        NotificationEntity.createNew(
            eventKey = "test.event",
            title = "Test Title",
            message = "Test Message",
            userId = MockConstants.USER_ID_INT,
        )

    fun buildNotificationDTO(): NotificationDTO {
        val entity = buildNotificationEntity()
        return NotificationDTO(
            id = entity.id,
            eventKey = entity.eventKey,
            title = entity.title,
            message = entity.message,
            status = entity.status,
            readAt = entity.readAt,
            action = entity.action,
            userId = MockUserFactory.build(),
            createdAt = LocalDateTime.now(),
        )
    }
}
