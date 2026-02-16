package io.github.pedroermarinho.comandalivre.domain.dtos.product

import io.github.pedroermarinho.shared.valueobject.EntityId

data class ProductModifierGroupDTO(
    val id: EntityId,
    val name: String,
    val minSelection: Int,
    val maxSelection: Int,
    val displayOrder: Int,
    val options: List<ProductModifierOptionDTO>,
)
