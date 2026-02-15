package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyreport

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.EmployeeProjectAssignmentResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.ProjectResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.weatherstatus.WeatherStatusResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Schema(description = "Representação detalhada de um relatório diário de projeto.")
data class DailyReportResponse(
    @param:Schema(description = "ID público do relatório.")
    val id: UUID,
    @param:Schema(description = "Detalhes do projeto ao qual o relatório se refere.")
    val project: ProjectResponse,
    @param:Schema(description = "Data a que o relatório se refere.")
    val reportDate: LocalDate,
    @param:Schema(description = "Observações gerais sobre o dia no projeto.")
    val generalObservations: String?,
    @param:Schema(description = "Status de tempo no período da manhã.")
    val morningWeather: WeatherStatusResponse?,
    @param:Schema(description = "Status de tempo no período da tarde.")
    val afternoonWeather: WeatherStatusResponse?,
    @param:Schema(description = "Horário de início das atividades no dia.")
    val workStartTime: LocalTime?,
    @param:Schema(description = "Horário de início do intervalo de almoço.")
    val lunchStartTime: LocalTime?,
    @param:Schema(description = "Horário de término do intervalo de almoço.")
    val lunchEndTime: LocalTime?,
    @param:Schema(description = "Horário de término das atividades no dia.")
    val workEndTime: LocalTime?,
    @param:Schema(description = "Detalhes da alocação do funcionário que está reportando.")
    val reportedByAssignment: EmployeeProjectAssignmentResponse?,
    @param:Schema(description = "Data e hora de criação do relatório.")
    val createdAt: LocalDateTime,
)
