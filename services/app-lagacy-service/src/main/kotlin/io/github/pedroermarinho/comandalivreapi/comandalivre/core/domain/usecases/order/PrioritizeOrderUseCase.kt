package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class PrioritizeOrderUseCase(
    private val orderRepository: OrderRepository,
) {
    fun execute(
        orderId: UUID,
        priorityLevel: Int,
    ) {
        val order = orderRepository.getById(orderId).getOrThrow()
        orderRepository.save(order.updatePriorityLevel(priorityLevel)).getOrThrow()
    }
}
