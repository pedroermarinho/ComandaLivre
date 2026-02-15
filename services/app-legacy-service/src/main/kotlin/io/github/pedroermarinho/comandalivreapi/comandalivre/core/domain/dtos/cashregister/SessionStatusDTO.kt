package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime

data class SessionStatusDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
)
