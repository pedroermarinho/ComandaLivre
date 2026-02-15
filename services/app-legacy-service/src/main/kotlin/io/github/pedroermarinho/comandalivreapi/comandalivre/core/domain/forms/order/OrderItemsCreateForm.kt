package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.order

import java.util.*

data class OrderItemsCreateForm(
    val productId: UUID,
    val notes: String? = null,
    val selectedModifierOptionIds: List<UUID> = emptyList(),
)
