package io.github.pedroermarinho.prumodigital.domain.dtos

import io.github.pedroermarinho.shared.valueobject.EntityId
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
