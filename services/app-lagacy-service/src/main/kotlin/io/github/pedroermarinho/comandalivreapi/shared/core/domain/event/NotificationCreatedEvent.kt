package io.github.pedroermarinho.comandalivreapi.shared.core.domain.event

data class NotificationCreatedEvent(
    val userId: Int,
    val title: String,
    val message: String,
    val eventKey: String,
    val action: Map<String, String>? = null,
)
