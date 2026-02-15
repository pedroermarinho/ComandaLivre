package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class UserDTO(
    val id: EntityId,
    val sub: String,
    val name: String,
    val email: String,
    val avatarAssetId: Int?,
    val featureKeys: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
)
