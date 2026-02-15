package io.github.pedroermarinho.comandalivreapi.shared.core.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.EmployeeInviteCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NotificationCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.notification.CreateNotificationUseCase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationCreationListener(
    private val createNotificationUseCase: CreateNotificationUseCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val log = KotlinLogging.logger {}

    @Async
    @EventListener
    fun onEmployeeInviteCreated(event: EmployeeInviteCreatedEvent) {
        if (event.userId == null) {
            log.info { "Convite criado para um e-mail sem usuário associado (${event.recipientEmail}), nenhuma notificação no app será criada." }
            return
        }
        applicationEventPublisher.publishEvent(
            NotificationCreatedEvent(
                userId = event.userId,
                eventKey = "employee.invite.created",
                title = "Você recebeu um novo convite!",
                message = "A empresa '${event.companyName}' convidou você para o cargo de '${event.roleName}'.",
            ),
        )
    }

    @Async
    @EventListener
    fun onNewUserRegistered(event: NewUserRegisteredEvent) {
        applicationEventPublisher.publishEvent(
            NotificationCreatedEvent(
                userId = event.userId,
                eventKey = "user.registered.welcome",
                title = "Bem-vindo(a) ao Comanda Livre!",
                message = "Olá, ${event.name}! Sua conta foi criada com sucesso. Explore tudo que nossa plataforma tem a oferecer.",
            ),
        )
    }

    @Async
    @EventListener
    fun onCompanyCreated(event: CompanyCreatedEvent) {
        log.info { "Gerando notificação de sucesso na criação da empresa para o proprietário: ${event.ownerEmail}" }
        applicationEventPublisher.publishEvent(
            NotificationCreatedEvent(
                userId = event.ownerId,
                eventKey = "company.creation.success",
                title = "Sua empresa foi criada!",
                message = "Parabéns, ${event.ownerName}! A empresa '${event.companyName}' está pronta. Comece a configurá-la agora mesmo.",
            ),
        )
    }
}
