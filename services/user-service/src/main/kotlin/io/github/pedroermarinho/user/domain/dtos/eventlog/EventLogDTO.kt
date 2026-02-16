package io.github.pedroermarinho.user.domain.dtos.eventlog

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class EventLogDTO(
    val id: EntityId,
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
