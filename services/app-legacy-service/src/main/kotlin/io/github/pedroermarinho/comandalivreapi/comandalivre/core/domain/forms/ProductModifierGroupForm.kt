package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms

data class ProductModifierGroupForm(
    val name: String,
    val minSelection: Int,
    val maxSelection: Int,
    val displayOrder: Int,
)
