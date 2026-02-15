package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.shared.valueobject.EntityId

interface OrderItemModifierRepository {
    fun create(
        orderItemId: Int,
        modifierOptionId: Int,
    ): Result<EntityId>
}
