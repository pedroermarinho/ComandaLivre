package io.github.pedroermarinho.shared.dtos.error

data class ErrorDTO(
    val status: Int,
    val message: String,
    val details: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val errors: List<FieldErrorDto>? = null,
)
