package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.version

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime

data class VersionDTO(
    val id: EntityId,
    val version: String,
    val platform: String,
    val createdAt: LocalDateTime,
)
