package io.github.pedroermarinho.comandalivre.domain.enums

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
import io.github.pedroermarinho.shared.exceptions.NotFoundException

enum class OrderStatusEnum(
    val value: String,
) {
    PENDING_CONFIRMATION("pending_confirmation"),
    IN_PREPARATION("in_preparation"),
    READY_FOR_DELIVERY("ready_for_delivery"),
    DELIVERED_SERVED("delivered_served"),
    ITEM_CANCELED("item_canceled"),
    RETURNED("returned"),
    ;

    companion object {
        fun fromValue(value: String): Result<OrderStatusEnum> =
            entries
                .find { it.value == value }
                ?.let { Result.success(it) }
                ?: Result.failure(NotFoundException("Status de pedido não encontrado: $value"))

        fun from(vo: OrderStatus): Result<OrderStatusEnum> = this.fromValue(vo.key.value)
    }

    fun isCanceled(): Boolean = this == ITEM_CANCELED
}
