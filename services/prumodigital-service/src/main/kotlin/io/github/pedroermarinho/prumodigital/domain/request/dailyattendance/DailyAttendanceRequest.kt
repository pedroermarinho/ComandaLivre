package io.github.pedroermarinho.prumodigital.domain.request.dailyattendance

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalTime
import java.util.UUID

@Schema(description = "Formulário para registrar a presença e horários de um funcionário em um relatório diário.")
data class DailyAttendanceRequest(
    @field:NotNull(message = "O ID do relatório diário é obrigatório.")
    @param:Schema(description = "ID público do relatório diário ao qual esta presença se refere.", required = true)
    val dailyReportId: UUID,
    @field:NotNull(message = "A indicação de presença é obrigatória.")
    @param:Schema(description = "Indica se o funcionário esteve presente (true) ou ausente (false).")
    val present: Boolean,
    @param:Schema(description = "Horário de chegada do funcionário.", example = "08:00:00")
    val arrivalTime: LocalTime?,
    @param:Schema(description = "Horário de saída do funcionário.", example = "17:00:00")
    val departureTime: LocalTime?,
    @param:Schema(description = "Observações sobre a presença/ausência do funcionário.", example = "Chegou atrasado devido ao trânsito.")
    val attendanceNote: String?,
)
