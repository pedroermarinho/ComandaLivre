package io.github.pedroermarinho.prumodigital.domain.dtos

import io.github.pedroermarinho.user.domain.dtos.asset.AssetDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class ActivityAttachmentDTO(
    val id: EntityId,
    val dailyActivity: DailyActivityDTO,
    val asset: AssetDTO,
    val description: String?,
    val createdAt: LocalDateTime,
)
