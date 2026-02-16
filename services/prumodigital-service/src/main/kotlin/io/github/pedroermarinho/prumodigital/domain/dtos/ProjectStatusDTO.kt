package io.github.pedroermarinho.prumodigital.domain.dtos

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class ProjectStatusDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
)
