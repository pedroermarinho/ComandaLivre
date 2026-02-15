package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms

import jakarta.validation.constraints.NotBlank

data class CancelForm(
    @field:NotBlank
    val reason: String,
)
