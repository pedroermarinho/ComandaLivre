package io.github.pedroermarinho.user.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.user.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.user.domain.event.EmployeeInviteCreatedEvent
import io.github.pedroermarinho.user.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.user.domain.forms.notification.NotificationForm
import io.github.pedroermarinho.user.domain.usecases.notification.CreateNotificationUseCase
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NotificationCreationListener(
    private val createNotificationUseCase: CreateNotificationUseCase,
) {
    private val log = KotlinLogging.logger {}

    @Async
    @TransactionalEventListener
    fun onEmployeeInviteCreated(event: EmployeeInviteCreatedEvent) {
        if (event.userId == null) {
            log.info { "Convite criado para um e-mail sem usuário associado (${event.recipientEmail}), nenhuma notificação no app será criada." }
            return
        }
        val form =
            NotificationForm(
                userId = event.userId,
                eventKey = "employee.invite.created",
                title = "Você recebeu um novo convite!",
                message = "A empresa '${event.companyName}' convidou você para o cargo de '${event.roleName}'.",
            )
        createNotificationUseCase.execute(form).getOrThrow()
    }

    @Async
    @TransactionalEventListener
    fun onNewUserRegistered(event: NewUserRegisteredEvent) {
        val form =
            NotificationForm(
                userId = event.userId,
                eventKey = "user.registered.welcome",
                title = "Bem-vindo(a) ao Comanda Livre!",
                message = "Olá, ${event.name}! Sua conta foi criada com sucesso. Explore tudo que nossa plataforma tem a oferecer.",
            )
        createNotificationUseCase.execute(form).getOrThrow()
    }

    @Async
    @TransactionalEventListener
    fun onCompanyCreated(event: CompanyCreatedEvent) {
        log.info { "Gerando notificação de sucesso na criação da empresa para o proprietário: ${event.ownerEmail}" }
        val form =
            NotificationForm(
                userId = event.ownerId,
                eventKey = "company.creation.success",
                title = "Sua empresa foi criada!",
                message = "Parabéns, ${event.ownerName}! A empresa '${event.companyName}' está pronta. Comece a configurá-la agora mesmo.",
            )
        createNotificationUseCase.execute(form).getOrThrow()
    }
}
