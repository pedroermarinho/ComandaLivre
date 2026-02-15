package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyreport

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Schema(description = "Formulário para a criação ou atualização de um relatório diário de obra (RDO).")
data class DailyReportRequest(
    @field:NotNull(message = "O ID do projeto é obrigatório.")
    @param:Schema(description = "ID público do projeto ao qual o relatório se refere.", required = true)
    val projectId: UUID,
    @field:NotNull(message = "A data do relatório é obrigatória.")
    @field:PastOrPresent(message = "A data do relatório não pode ser no futuro.")
    @param:Schema(description = "Data a que o relatório se refere.", required = true)
    val reportDate: LocalDate,
    @param:Schema(description = "Observações gerais sobre o dia no projeto.")
    val generalObservations: String?,
    @param:Schema(description = "ID público do status de tempo para o período da manhã.")
    val morningWeatherId: UUID?,
    @param:Schema(description = "ID público do status de tempo para o período da tarde.")
    val afternoonWeatherId: UUID?,
    @param:Schema(description = "Horário de início das atividades no dia.")
    val workStartTime: LocalTime?,
    @param:Schema(description = "Horário de início do intervalo de almoço.")
    val lunchStartTime: LocalTime?,
    @param:Schema(description = "Horário de término do intervalo de almoço.")
    val lunchEndTime: LocalTime?,
    @param:Schema(description = "Horário de término das atividades no dia.")
    val workEndTime: LocalTime?,
    @param:Schema(description = "ID público da alocação do funcionário que está reportando.")
    val reportedByAssignmentId: UUID?,
)
