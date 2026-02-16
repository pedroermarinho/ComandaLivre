package io.github.pedroermarinho.comandalivre.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.TableStatusEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.UpdateTableStatusUseCase
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TableStatusEventListener(
    private val updateTableStatusUseCase: UpdateTableStatusUseCase,
) {
    private val log = KotlinLogging.logger {}

    @Async
    @TransactionalEventListener
    fun onTableStatusEvent(event: TableStatusEvent) {
        log.info { "Processando evento de status da mesa: ${event.tableId} - Iniciando atualização" }
        updateTableStatusUseCase.execute(event.tableId).getOrThrow()
        log.info { "Processamento do evento de status da mesa: ${event.tableId} - Atualização concluída" }
    }
}
