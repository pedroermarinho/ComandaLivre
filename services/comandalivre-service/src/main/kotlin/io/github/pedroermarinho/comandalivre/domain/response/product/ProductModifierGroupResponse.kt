package io.github.pedroermarinho.comandalivre.domain.response.product

import java.util.UUID

data class ProductModifierGroupResponse(
    val id: UUID,
    val name: String,
    val minSelection: Int,
    val maxSelection: Int,
    val displayOrder: Int,
    val options: List<ProductModifierOptionResponse>,
)
