package io.github.pedroermarinho.comandalivreapi.shared.core.domain.event

data class CriticalErrorOccurredEvent(
    val location: String,
    val errorMessage: String,
    val stackTrace: String? = null,
)
