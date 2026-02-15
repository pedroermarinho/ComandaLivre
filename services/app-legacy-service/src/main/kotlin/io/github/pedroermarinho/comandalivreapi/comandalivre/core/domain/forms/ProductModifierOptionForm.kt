package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms

import java.math.BigDecimal

data class ProductModifierOptionForm(
    val name: String,
    val priceChange: BigDecimal,
    val isDefault: Boolean,
    val displayOrder: Int,
)
