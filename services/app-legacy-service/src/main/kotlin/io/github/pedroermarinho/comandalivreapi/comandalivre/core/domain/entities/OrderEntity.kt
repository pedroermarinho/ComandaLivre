package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.*
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.MonetaryValue
import io.github.pedroermarinho.shared.valueobject.UserId
import java.math.BigDecimal
import java.util.UUID

data class OrderEntity(
    val id: EntityId,
    val commandId: CommandId,
    val productId: ProductId,
    val status: OrderStatus,
    val notes: OrderNotes?,
    val audit: EntityAudit,
    val priorityLevel: OrderPriority = OrderPriority(0),
    val basePriceAtOrder: MonetaryValue?,
    val totalModifiersPriceAtOrder: MonetaryValue?,
    val cancellationReason: String? = null,
    val cancelledByUserId: UserId? = null,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            commandId: Int,
            productId: Int,
            status: OrderStatus,
            notes: String?,
            priorityLevel: Int = 0,
            basePriceAtOrder: BigDecimal?,
            totalModifiersPriceAtOrder: BigDecimal?,
        ): OrderEntity =
            OrderEntity(
                id = EntityId.createNew(publicId = publicId),
                commandId = CommandId(commandId),
                productId = ProductId(productId),
                status = status,
                notes = notes?.let { OrderNotes(it) },
                audit = EntityAudit.createNew(),
                priorityLevel = OrderPriority(priorityLevel),
                basePriceAtOrder = basePriceAtOrder?.let { MonetaryValue(it) },
                totalModifiersPriceAtOrder = totalModifiersPriceAtOrder?.let { MonetaryValue(it) },
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        notes: String?,
        priorityLevel: Int,
        basePriceAtOrder: BigDecimal?,
        totalModifiersPriceAtOrder: BigDecimal?,
    ): OrderEntity =
        this.copy(
            notes = notes?.let { OrderNotes(it) },
            priorityLevel = OrderPriority(priorityLevel),
            basePriceAtOrder = basePriceAtOrder?.let { MonetaryValue(it) },
            totalModifiersPriceAtOrder = totalModifiersPriceAtOrder?.let { MonetaryValue(it) },
            cancellationReason = cancellationReason,
            audit = this.audit.update(),
        )

    fun updateStatus(status: OrderStatus): OrderEntity =
        this.copy(
            status = status,
            audit = this.audit.update(),
        )

    fun updatePriorityLevel(priorityLevel: Int): OrderEntity =
        this.copy(
            priorityLevel = OrderPriority(priorityLevel),
        )

    fun updateCancelInfo(
        reason: String?,
        cancelledByUserId: Int,
    ): OrderEntity =
        this.copy(
            cancellationReason = reason,
            cancelledByUserId = UserId(cancelledByUserId),
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
