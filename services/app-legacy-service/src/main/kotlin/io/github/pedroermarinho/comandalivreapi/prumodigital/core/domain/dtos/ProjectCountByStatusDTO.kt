package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import java.util.UUID

data class ProjectCountByStatusDTO(
    val statusId: UUID,
    val statusName: String,
    val projectCount: Int,
)
