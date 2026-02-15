package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class FeatureDTO(
    val id: EntityId,
    val featureKey: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
)
