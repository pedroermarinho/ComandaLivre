package io.github.pedroermarinho.comandalivre.domain.usecases.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.usecases.user.CurrentUserUseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class ChangeStatusOrderUseCase(
    private val orderRepository: OrderRepository,
    private val searchOrderUseCase: SearchOrderUseCase,
    private val searchStatusOrderUseCase: SearchStatusOrderUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
) {
    fun execute(
        publicId: UUID,
        status: String,
        reason: String? = null,
    ): Result<Unit> =
        runCatching {
            var order = searchOrderUseCase.getEntityById(publicId).getOrThrow()
            val status = searchStatusOrderUseCase.getByKey(status).getOrThrow()

            if (OrderStatusEnum.from(status).getOrThrow().isCanceled()) {
                order =
                    order.updateCancelInfo(
                        reason = reason,
                        cancelledByUserId = currentUserUseCase.getUserId().getOrThrow(),
                    )
            }

            orderRepository.save(order.updateStatus(status)).getOrThrow()
        }

    fun closeAll(commandId: Int): Result<Unit> =
        runCatching {
            val orders = searchOrderUseCase.getEntityAllList(commandId).getOrThrow()
            val status = searchStatusOrderUseCase.getByEnum(OrderStatusEnum.DELIVERED_SERVED).getOrThrow()
            orders.forEach {
                orderRepository.save(it.updateStatus(status)).getOrThrow()
            }
        }
}
