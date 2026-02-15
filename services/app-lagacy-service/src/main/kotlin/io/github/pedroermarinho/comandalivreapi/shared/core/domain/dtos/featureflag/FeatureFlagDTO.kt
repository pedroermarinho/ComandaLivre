package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.featureflag

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
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
