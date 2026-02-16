package io.github.pedroermarinho.user.domain.dtos.group

import io.github.pedroermarinho.user.domain.dtos.feature.FeatureDTO
import java.time.LocalDateTime

data class GroupFeaturePermissionDTO(
    val featureGroup: GroupDTO,
    val feature: FeatureDTO,
    val isEnabled: Boolean,
    val grantedAt: LocalDateTime,
    val createdAt: LocalDateTime,
)
