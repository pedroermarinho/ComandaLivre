package io.github.pedroermarinho.user.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.user.domain.event.NotificationCreatedEvent
import io.github.pedroermarinho.user.domain.forms.notification.NotificationForm
import io.github.pedroermarinho.user.domain.usecases.notification.CreateNotificationUseCase
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NotificationEventListener(
    private val createNotificationUseCase: CreateNotificationUseCase,
) {
    private val log = KotlinLogging.logger {}

    @Async
    @TransactionalEventListener
    fun handleNotificationCreatedEvent(event: NotificationCreatedEvent) {
        runCatching {
            val form =
                NotificationForm(
                    userId = event.userId,
                    title = event.title,
                    message = event.message,
                    eventKey = event.eventKey,
                    action = event.action,
                )
            createNotificationUseCase.execute(form)
                .onSuccess { log.info { "Notificação '${event.eventKey}' para o usuário ${event.userId} criada com sucesso" } }
                .onFailure {
                    log.error(it) {
                        "Falha ao processar a notificação '${event.eventKey}' para o usuário ${event.userId}. Causa: ${it.message}"
                    }
                }
        }.onFailure {
            log.error(it) {
                "Falha inesperada ao processar a notificação '${event.eventKey}' para o usuário ${event.userId}. Causa: ${it.message}"
            }
        }
    }
}
