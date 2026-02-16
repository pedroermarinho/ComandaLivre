package io.github.pedroermarinho.user.domain.response.notification

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val eventKey: String,
    val title: String,
    val message: String,
    val status: Boolean = false,
    val readAt: OffsetDateTime? = null,
    val createdAt: LocalDateTime,
)
