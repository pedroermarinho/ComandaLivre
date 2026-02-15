package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms

import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class DailyReportForm(
    val projectId: Int,
    val reportDate: LocalDate,
    val generalObservations: String?,
    val morningWeatherId: Int?,
    val afternoonWeatherId: Int?,
    val workStartTime: LocalTime?,
    val lunchStartTime: LocalTime?,
    val lunchEndTime: LocalTime?,
    val workEndTime: LocalTime?,
    val reportedByAssignmentId: Int?,
)
