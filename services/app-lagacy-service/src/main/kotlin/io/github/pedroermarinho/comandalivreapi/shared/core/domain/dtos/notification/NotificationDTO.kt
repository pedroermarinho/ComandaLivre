package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.notification

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

data class NotificationDTO(
    val id: EntityId,
    val eventKey: String,
    val title: String,
    val message: String,
    val status: Boolean = false,
    val readAt: OffsetDateTime? = null,
    val action: Map<String, String>? = null,
    val userId: UserDTO? = null,
    val createdAt: LocalDateTime,
)
