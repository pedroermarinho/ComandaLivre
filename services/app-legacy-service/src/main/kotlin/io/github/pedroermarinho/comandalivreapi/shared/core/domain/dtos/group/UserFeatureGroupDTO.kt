package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
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
