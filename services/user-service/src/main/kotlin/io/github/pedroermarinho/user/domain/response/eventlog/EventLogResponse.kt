package io.github.pedroermarinho.user.domain.response.eventlog

import java.time.LocalDateTime
import java.util.*

data class EventLogResponse(
    val id: UUID,
    val eventKey: String,
    val eventTitle: String,
    val eventDescription: String?,
    val actorUserSub: String?,
    val targetEntityType: String?,
    val targetEntityKey: String?,
    val tags: List<String?>?,
    val ipAddress: String?,
    val loggedAt: LocalDateTime,
)
