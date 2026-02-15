package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class DailyReportDTO(
    val id: EntityId,
    val reportDate: LocalDate,
    val generalObservations: String?,
    val morningWeather: WeatherStatusDTO?,
    val afternoonWeather: WeatherStatusDTO?,
    val project: ProjectDTO,
    val workStartTime: LocalTime?,
    val lunchStartTime: LocalTime?,
    val lunchEndTime: LocalTime?,
    val workEndTime: LocalTime?,
    val reportedByAssignment: EmployeeProjectAssignmentDTO?,
    val createdAt: LocalDateTime,
)
