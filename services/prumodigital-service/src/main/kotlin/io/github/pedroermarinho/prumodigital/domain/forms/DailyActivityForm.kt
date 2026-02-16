package io.github.pedroermarinho.prumodigital.domain.forms

import java.util.*

data class DailyActivityForm(
    val dailyReportId: Int,
    val activityDescription: String,
    val statusId: Int,
    val locationDescription: String?,
    val responsibleEmployeeAssignmentId: Int?,
)
