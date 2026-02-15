package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.TableStatusEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.SearchTableUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class ChangeCommandTableUseCase(
    private val commandRepository: CommandRepository,
    private val searchCommandUseCase: SearchCommandUseCase,
    private val searchTableUseCase: SearchTableUseCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val log = KotlinLogging.logger {}

    fun execute(
        commandPublicId: UUID,
        newTablePublicId: UUID,
    ): Result<Unit> =
        runCatching {
            log.info { "Iniciando a troca de mesa para a comanda $commandPublicId para a mesa $newTablePublicId" }

            val command = searchCommandUseCase.getEntityById(commandPublicId).getOrThrow()
            val newTable = searchTableUseCase.getEntityById(newTablePublicId).getOrThrow()

            log.info { "Trocando a comanda ${command.id.internalId} para a mesa ${newTable.id.internalId}" }
            commandRepository.save(command.updateTable(newTable).getOrThrow()).getOrThrow()

            applicationEventPublisher.publishEvent(
                TableStatusEvent(tableId = command.tableId.value),
            )

            applicationEventPublisher.publishEvent(
                TableStatusEvent(tableId = newTable.id.internalId),
            )
        }
}
