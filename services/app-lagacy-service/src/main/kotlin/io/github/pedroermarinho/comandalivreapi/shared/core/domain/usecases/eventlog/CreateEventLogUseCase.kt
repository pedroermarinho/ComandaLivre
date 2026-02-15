package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.eventlog

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.EventLogEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.eventlog.EventLogForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.EventLogRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateEventLogUseCase(
    private val eventLogRepository: EventLogRepository,
) {
    private val log = KotlinLogging.logger {}

    fun execute(form: EventLogForm) {
        log.info { "Recebido evento de domínio para logar: ${form.eventKey}" }
        eventLogRepository
            .save(
                EventLogEntity.createNew(
                    publicId = form.publicId,
                    eventKey = form.eventKey,
                    eventTitle = form.eventTitle,
                    eventDescription = form.eventDescription,
                    actorUserSub = form.actorUserSub,
                    targetEntityType = form.targetEntityType,
                    targetEntityKey = form.targetEntityKey,
                    tags = form.tags,
                    ipAddress = form.ipAddress,
                ),
            ).onFailure { error ->
                log.error(error) { "Falha crítica ao persistir o log do evento ${form.eventKey}" }
            }
    }
}
