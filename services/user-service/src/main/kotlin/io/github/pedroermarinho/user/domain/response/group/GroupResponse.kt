package io.github.pedroermarinho.user.domain.response.group

import java.time.LocalDateTime
import java.util.UUID

data class GroupResponse(
    val id: UUID,
    val groupKey: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
)
