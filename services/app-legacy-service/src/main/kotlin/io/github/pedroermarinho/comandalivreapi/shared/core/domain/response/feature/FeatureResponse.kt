package io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.feature

import java.time.LocalDateTime
import java.util.UUID

data class FeatureResponse(
    val id: UUID,
    val featureKey: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
)
