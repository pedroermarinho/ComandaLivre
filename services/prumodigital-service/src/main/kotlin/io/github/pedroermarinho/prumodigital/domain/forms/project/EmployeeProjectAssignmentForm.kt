package io.github.pedroermarinho.prumodigital.domain.forms.project

import java.time.LocalDate

data class EmployeeProjectAssignmentForm(
    val employeeId: Int,
    val projectId: Int,
    val startDate: LocalDate,
    val isAdmin: Boolean? = false,
    val roleInProjectId: Int,
    val assignmentStartDate: LocalDate = LocalDate.now(),
    val isActiveAssignment: Boolean = true,
    val isProjectAdmin: Boolean = false,
)
