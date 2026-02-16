package io.github.pedroermarinho.comandalivre.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.CommandEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.command.CalculatorCommandUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table.UpdateTableStatusUseCase
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class CommandEventListener(
    private val updateTableStatusUseCase: UpdateTableStatusUseCase,
    private val calculatorCommandUseCase: CalculatorCommandUseCase,
) {
    private val log = KotlinLogging.logger {}

    @Async
    @TransactionalEventListener
    fun onOrderEvent(event: CommandEvent) {
        log.info { "Processando evento de comanda: ${event.commandId}" }
        calculatorCommandUseCase.calculateTotal(event.commandId).getOrThrow()
        log.info { "Processamento do evento de comanda concluído: ${event.commandId}" }
    }
}
