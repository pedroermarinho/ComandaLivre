package io.github.pedroermarinho.shared.event 

data class CriticalErrorOccurredEvent(
    val location: String,
    val errorMessage: String,
    val stackTrace: String? = null,
)