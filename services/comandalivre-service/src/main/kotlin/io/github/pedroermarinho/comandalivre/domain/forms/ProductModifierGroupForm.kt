package io.github.pedroermarinho.comandalivre.domain.forms

data class ProductModifierGroupForm(
    val name: String,
    val minSelection: Int,
    val maxSelection: Int,
    val displayOrder: Int,
)
