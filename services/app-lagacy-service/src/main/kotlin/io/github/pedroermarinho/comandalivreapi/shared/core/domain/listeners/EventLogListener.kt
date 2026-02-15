package io.github.pedroermarinho.comandalivreapi.shared.core.domain.listeners

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.DomainEventOccurredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.eventlog.EventLogForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.eventlog.CreateEventLogUseCase
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EventLogListener(
    private val createEventLogUseCase: CreateEventLogUseCase,
) {
    @Async
    @EventListener
    fun onDomainEventOccurred(event: DomainEventOccurredEvent) {
        createEventLogUseCase.execute(event.form)
    }

    @Async
    @EventListener
    fun onCompanyCreated(event: CompanyCreatedEvent) {
        val form =
            EventLogForm(
                eventKey = "company.created",
                eventTitle = "Empresa criada",
                eventDescription = "Empresa ${event.companyName} foi criado por ${event.ownerName}",
                actorUserSub = event.ownerSub,
                targetEntityType = "COMPANY",
                targetEntityKey = event.companyPublicId.toString(),
                tags = listOf("company", "creation"),
            )
        createEventLogUseCase.execute(form)
    }

    @Async
    @EventListener
    fun onNewUserRegistered(event: NewUserRegisteredEvent) {
        val form =
            EventLogForm(
                eventKey = "user.created",
                eventTitle = "Usuário criado",
                eventDescription = "usuário ${event.name} foi criado",
                actorUserSub = event.sub,
                targetEntityType = "USER",
                targetEntityKey = event.userPublicId.toString(),
                tags = listOf("user", "creation"),
            )
        createEventLogUseCase.execute(form)
    }
}
