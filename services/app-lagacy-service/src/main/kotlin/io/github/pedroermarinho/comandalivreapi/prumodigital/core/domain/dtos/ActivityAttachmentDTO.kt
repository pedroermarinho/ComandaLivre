package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.asset.AssetDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class ActivityAttachmentDTO(
    val id: EntityId,
    val dailyActivity: DailyActivityDTO,
    val asset: AssetDTO,
    val description: String?,
    val createdAt: LocalDateTime,
)
