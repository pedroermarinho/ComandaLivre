package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class DailyActivityDTO(
    val id: EntityId,
    val dailyReport: DailyReportDTO,
    val activityDescription: String,
    val status: DailyActivityStatusDTO,
    val locationDescription: String?,
    val responsibleEmployeeAssignment: EmployeeProjectAssignmentDTO?,
    val createdAt: LocalDateTime,
)
