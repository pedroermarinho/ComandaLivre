package io.github.pedroermarinho.comandalivre.domain.dtos.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDTO(
    val id: EntityId,
    val product: ProductDTO,
    val command: CommandDTO,
    val status: OrderStatusDTO,
    val notes: String?,
    val priorityLevel: Int,
    val cancellationReason: String?,
    val basePriceAtOrder: BigDecimal?,
    val totalModifiersPriceAtOrder: BigDecimal?,
    val createdAt: LocalDateTime,
)
