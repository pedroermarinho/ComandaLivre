package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.error

data class ErrorDTO(
    val status: Int,
    val message: String,
    val details: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val errors: List<FieldErrorDto>? = null,
)
