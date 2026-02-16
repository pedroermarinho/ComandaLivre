package io.github.pedroermarinho.comandalivre.domain.forms

import java.math.BigDecimal

data class ProductModifierOptionForm(
    val name: String,
    val priceChange: BigDecimal,
    val isDefault: Boolean,
    val displayOrder: Int,
)
