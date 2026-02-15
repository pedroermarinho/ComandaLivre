package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class DailyAttendanceDTO(
    val id: EntityId,
    val dailyReport: DailyReportDTO,
    val employeeAssignment: EmployeeProjectAssignmentDTO,
    val present: Boolean,
    val arrivalTime: LocalTime?,
    val departureTime: LocalTime?,
    val attendanceNote: String?,
    val createdAt: LocalDateTime,
)
