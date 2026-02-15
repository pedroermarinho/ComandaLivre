package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyattendance

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.EmployeeProjectAssignmentResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Schema(description = "Representação detalhada da presença de um funcionário em um relatório diário.")
data class DailyAttendanceResponse(
    @param:Schema(description = "ID público do registro de presença.")
    val id: UUID,
    @param:Schema(description = "Detalhes da alocação do funcionário no projeto.")
    val employeeAssignment: EmployeeProjectAssignmentResponse,
    @param:Schema(description = "Indica se o funcionário esteve presente (true) ou ausente (false).")
    val present: Boolean,
    @param:Schema(description = "Horário de chegada do funcionário.")
    val arrivalTime: LocalTime?,
    @param:Schema(description = "Horário de saída do funcionário.")
    val departureTime: LocalTime?,
    @param:Schema(description = "Observações sobre a presença/ausência do funcionário.")
    val attendanceNote: String?,
    @param:Schema(description = "Data e hora de criação do registro.")
    val createdAt: LocalDateTime,
)
