package io.github.pedroermarinho.comandalivre.domain.usecases.command

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.TotalizeCommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order.SearchOrderUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CalculatorCommandUseCase(
    private val searchOrderUseCase: SearchOrderUseCase,
    private val searchCommandUseCase: SearchCommandUseCase,
    private val commandRepository: CommandRepository,
) {
    private val log = KotlinLogging.logger {}

    fun calculateTotal(commandId: Int): Result<TotalizeCommandDTO> =
        runCatching {
            val command = searchCommandUseCase.getEntityById(commandId).getOrThrow()
            val orders = searchOrderUseCase.getAllList(commandId).getOrThrow()
            val update = command.updateTotalAmount(orders)
            commandRepository.save(update).getOrThrow()
            return Result.success(TotalizeCommandDTO(update.totalAmount?.value!!, orders.size))
        }
}
