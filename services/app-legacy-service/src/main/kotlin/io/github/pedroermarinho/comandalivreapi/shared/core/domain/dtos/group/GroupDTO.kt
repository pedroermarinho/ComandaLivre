package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class GroupDTO(
    val id: EntityId,
    val groupKey: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
)
