package io.github.pedroermarinho.comandalivre.domain.forms

import jakarta.validation.constraints.NotBlank

data class CancelForm(
    @field:NotBlank
    val reason: String,
)
