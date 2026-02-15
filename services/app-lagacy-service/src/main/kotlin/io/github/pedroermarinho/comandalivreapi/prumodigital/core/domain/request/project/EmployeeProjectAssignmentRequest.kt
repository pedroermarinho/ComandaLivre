package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.project

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.UUID

@Schema(description = "Formulário para alocar um funcionário a um projeto.")
data class EmployeeProjectAssignmentRequest(
    @field:NotNull(message = "O ID do funcionário é obrigatório.")
    @param:Schema(description = "ID público do funcionário a ser alocado.", required = true)
    val employeeId: UUID,
    @param:Schema(description = "Data de início da alocação do funcionário no projeto. Se não informada, usa a data atual.")
    val startDate: LocalDate? = null,
    @param:Schema(description = "Indica se o funcionário terá permissões de administrador no projeto.", defaultValue = "false")
    val isAdmin: Boolean? = false,
)
