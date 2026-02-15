package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
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
