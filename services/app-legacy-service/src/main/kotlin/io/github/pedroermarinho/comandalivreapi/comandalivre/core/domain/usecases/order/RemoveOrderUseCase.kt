package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.event.CommandEvent
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.CheckPermissionCompanyUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class RemoveOrderUseCase(
    private val orderRepository: OrderRepository,
    private val searchOrderUseCase: SearchOrderUseCase,
    private val checkPermissionCompanyUseCase: CheckPermissionCompanyUseCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val log = KotlinLogging.logger {}

    fun execute(orderId: UUID): Result<Unit> =
        runCatching {
            log.info { "Iniciando remoção de item do pedido: $orderId" }
            val order = searchOrderUseCase.getById(orderId).getOrThrow()
            checkPermissionCompanyUseCase.execute(order.command.table.companyId).getOrThrow()
            orderRepository.delete(order.id.internalId).getOrThrow()
            log.info { "Item do pedido removido com sucesso: $orderId" }

            applicationEventPublisher.publishEvent(
                CommandEvent(commandId = order.command.id.internalId),
            )
            Result.success(Unit)
        }
}
