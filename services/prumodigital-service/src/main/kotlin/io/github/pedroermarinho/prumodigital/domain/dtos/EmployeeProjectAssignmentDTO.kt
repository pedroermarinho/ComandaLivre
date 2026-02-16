package io.github.pedroermarinho.prumodigital.domain.dtos

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.RoleTypeDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class EmployeeProjectAssignmentDTO(
    val id: EntityId,
    val employee: EmployeeDTO,
    val project: ProjectDTO,
    val roleInProject: RoleTypeDTO?,
    val assignmentStartDate: LocalDate,
    val assignmentEndDate: LocalDate?,
    val isActiveAssignment: Boolean,
    val isProjectAdmin: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
