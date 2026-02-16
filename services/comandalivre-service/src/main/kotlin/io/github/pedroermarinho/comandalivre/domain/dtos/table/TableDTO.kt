package io.github.pedroermarinho.comandalivre.domain.dtos.table

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class TableDTO(
    val id: EntityId,
    val name: String,
    val numPeople: Int,
    val status: TableStatusDTO,
    val companyId: Int,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
