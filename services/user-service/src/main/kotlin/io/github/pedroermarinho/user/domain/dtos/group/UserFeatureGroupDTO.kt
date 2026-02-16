package io.github.pedroermarinho.user.domain.dtos.group

import io.github.pedroermarinho.user.domain.dtos.user.UserDTO
import java.time.LocalDateTime

data class UserFeatureGroupDTO(
    val user: UserDTO,
    val featureGroup: GroupDTO,
    val isActive: Boolean,
    val assignedAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val notes: String?,
    val createdAt: LocalDateTime,
)
