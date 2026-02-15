package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal

data class ProductModifierOptionEntity(
    val id: EntityId,
    val modifierGroupId: Int,
    val name: String,
    val priceChange: BigDecimal,
    val isDefault: Boolean,
    val displayOrder: Int,
    val imageAssetId: AssetId?,
    val audit: EntityAudit,
) {
    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
