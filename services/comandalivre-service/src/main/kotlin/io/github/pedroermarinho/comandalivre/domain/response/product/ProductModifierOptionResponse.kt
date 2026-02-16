package io.github.pedroermarinho.comandalivre.domain.response.product

import java.math.BigDecimal
import java.util.UUID

data class ProductModifierOptionResponse(
    val id: UUID,
    val name: String,
    val priceChange: BigDecimal,
    val isDefault: Boolean,
    val displayOrder: Int,
    val image: String?,
)
