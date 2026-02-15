package io.github.pedroermarinho.comandalivreapi.shared.core.domain.listeners

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NotificationCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.notification.NotificationForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification.CreateNotificationUseCase
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
    private val createNotificationUseCase: CreateNotificationUseCase,
) {
    @Async
    @EventListener
    fun handleNotificationCreatedEvent(event: NotificationCreatedEvent) {
        val form =
            NotificationForm(
                userId = event.userId,
                title = event.title,
                message = event.message,
                eventKey = event.eventKey,
                action = event.action,
            )
        createNotificationUseCase.execute(form).getOrThrow()
    }
}
