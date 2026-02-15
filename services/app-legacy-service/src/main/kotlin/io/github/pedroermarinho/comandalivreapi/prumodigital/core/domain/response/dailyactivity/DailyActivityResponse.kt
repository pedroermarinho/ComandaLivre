package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyreport.DailyReportResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.EmployeeProjectAssignmentResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de uma atividade diária de projeto.")
data class DailyActivityResponse(
    @param:Schema(description = "ID público da atividade.")
    val id: UUID,
    @param:Schema(description = "Detalhes do relatório diário ao qual a atividade pertence.")
    val dailyReport: DailyReportResponse,
    @param:Schema(description = "Descrição detalhada da atividade.")
    val activityDescription: String,
    @param:Schema(description = "Status atual da atividade.")
    val status: DailyActivityStatusResponse,
    @param:Schema(description = "Descrição do local específico da atividade.")
    val locationDescription: String?,
    @param:Schema(description = "Detalhes da alocação do funcionário responsável pela atividade.")
    val responsibleEmployeeAssignment: EmployeeProjectAssignmentResponse?,
    @param:Schema(description = "Data e hora de criação da atividade.")
    val createdAt: LocalDateTime,
)
