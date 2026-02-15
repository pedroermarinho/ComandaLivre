package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class OrderStatusDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
)
