package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class WeatherStatusDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
    val icon: String?,
    val createdAt: LocalDateTime,
)
