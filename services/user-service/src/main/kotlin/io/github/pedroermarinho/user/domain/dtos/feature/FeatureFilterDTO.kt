package io.github.pedroermarinho.user.domain.dtos.feature

import java.util.UUID

data class FeatureFilterDTO(
    val group: UUID? = null,
    val excludeGroup: UUID? = null,
)
