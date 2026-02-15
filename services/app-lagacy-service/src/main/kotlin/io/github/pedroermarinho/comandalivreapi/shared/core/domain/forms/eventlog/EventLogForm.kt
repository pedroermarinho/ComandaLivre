package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.eventlog

import java.util.*

data class EventLogForm(
    val publicId: UUID? = null,
    val eventKey: String,
    val eventTitle: String,
    val eventDescription: String? = null,
    val actorUserSub: String? = null,
    val targetEntityType: String? = null,
    val targetEntityKey: String? = null,
    val tags: List<String>? = null,
    val ipAddress: String? = null,
)
