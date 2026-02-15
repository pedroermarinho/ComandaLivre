package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId

interface OrderItemModifierRepository {
    fun create(
        orderItemId: Int,
        modifierOptionId: Int,
    ): Result<EntityId>
}
