package io.github.pedroermarinho.user.domain.dtos.featureflag

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class FeatureFlagDTO(
    val id: EntityId,
    val name: String,
    val description: String?,
    val keyFlag: String,
    val enabled: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
