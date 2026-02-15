package io.github.pedroermarinho.comandalivreapi.shared.core.domain.event

data class CustomSystemEvent(
    val title: String,
    val description: String,
    val level: EventLevel = EventLevel.INFO,
) {
    enum class EventLevel {
        INFO,
        WARN,
        URGENT,
    }
}
