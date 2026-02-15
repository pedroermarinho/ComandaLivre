package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId

data class ProductModifierGroupDTO(
    val id: EntityId,
    val name: String,
    val minSelection: Int,
    val maxSelection: Int,
    val displayOrder: Int,
    val options: List<ProductModifierOptionDTO>,
)
