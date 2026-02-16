package io.github.pedroermarinho.prumodigital.domain.response.project

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.RoleTypeResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada da alocação de um funcionário em um projeto.")
data class EmployeeProjectAssignmentResponse(
    @param:Schema(description = "ID público da alocação.")
    val id: UUID,
    @param:Schema(description = "Detalhes do funcionário alocado.")
    val employee: EmployeeResponse,
    @param:Schema(description = "Detalhes do projeto ao qual o funcionário está alocado.")
    val project: ProjectResponse,
    @param:Schema(description = "Detalhes do cargo do funcionário no projeto.")
    val roleInProject: RoleTypeResponse?,
    @param:Schema(description = "Data de início da alocação.")
    val assignmentStartDate: LocalDate,
    @param:Schema(description = "Data de término da alocação.")
    val assignmentEndDate: LocalDate?,
    @param:Schema(description = "Indica se a alocação está ativa.")
    val isActiveAssignment: Boolean,
    @param:Schema(description = "Indica se o funcionário é administrador do projeto.")
    val isProjectAdmin: Boolean,
    @param:Schema(description = "Data e hora de criação do registro.")
    val createdAt: LocalDateTime,
)
