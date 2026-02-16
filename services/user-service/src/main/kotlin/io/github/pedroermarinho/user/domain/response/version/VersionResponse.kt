package io.github.pedroermarinho.user.domain.response.version

import java.time.LocalDateTime
import java.util.UUID

data class VersionResponse(
    val id: UUID,
    val version: String,
    val platform: String,
    val createdAt: LocalDateTime,
)
