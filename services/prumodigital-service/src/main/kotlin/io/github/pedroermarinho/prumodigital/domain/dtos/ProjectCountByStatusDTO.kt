package io.github.pedroermarinho.prumodigital.domain.dtos

import java.util.UUID

data class ProjectCountByStatusDTO(
    val statusId: UUID,
    val statusName: String,
    val projectCount: Int,
)
