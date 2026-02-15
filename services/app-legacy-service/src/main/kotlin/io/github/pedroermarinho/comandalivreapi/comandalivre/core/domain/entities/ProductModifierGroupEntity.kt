package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId

data class ProductModifierGroupEntity(
    val id: EntityId,
    val productId: Int,
    val name: String,
    val minSelection: Int,
    val maxSelection: Int,
    val displayOrder: Int,
    val audit: EntityAudit,
) {
    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
