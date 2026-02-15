package io.github.pedroermarinho.shared.dtos.error

data class FieldErrorDto(
    val field: String,
    val errorMessage: String,
)
