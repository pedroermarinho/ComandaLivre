package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms

import java.time.LocalTime

data class DailyAttendanceForm(
    val dailyReportId: Int,
    val employeeAssignmentId: Int,
    val present: Boolean,
    val arrivalTime: LocalTime?,
    val departureTime: LocalTime?,
    val attendanceNote: String?,
)
