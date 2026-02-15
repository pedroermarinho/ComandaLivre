package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.error

data class FieldErrorDto(
    val field: String,
    val errorMessage: String,
)
