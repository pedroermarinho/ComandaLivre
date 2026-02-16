package io.github.pedroermarinho.user.domain.response.featureflag

import java.time.LocalDateTime
import java.util.UUID

data class FeatureFlagResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val keyFlag: String,
    val enabled: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
