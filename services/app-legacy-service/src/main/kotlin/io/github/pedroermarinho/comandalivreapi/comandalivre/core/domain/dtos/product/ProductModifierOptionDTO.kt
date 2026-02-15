package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal

data class ProductModifierOptionDTO(
    val id: EntityId,
    val name: String,
    val priceChange: BigDecimal,
    val isDefault: Boolean,
    val displayOrder: Int,
    val image: String?,
)
