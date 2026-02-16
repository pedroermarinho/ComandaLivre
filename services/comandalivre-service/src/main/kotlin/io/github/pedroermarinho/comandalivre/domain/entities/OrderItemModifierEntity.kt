package io.github.pedroermarinho.comandalivre.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal

data class OrderItemModifierEntity(
    val id: EntityId,
    val orderItemId: Int,
    val modifierOptionId: Int,
    val priceAtOrder: BigDecimal,
    val audit: EntityAudit,
) {
    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
