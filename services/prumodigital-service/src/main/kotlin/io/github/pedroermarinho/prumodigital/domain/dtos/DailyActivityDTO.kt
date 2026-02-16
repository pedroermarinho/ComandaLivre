package io.github.pedroermarinho.prumodigital.domain.dtos

import io.github.pedroermarinho.shared.valueobject.EntityId
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
