package io.github.pedroermarinho.comandalivre.domain.dtos.product

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class ProductCategoryDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
)
