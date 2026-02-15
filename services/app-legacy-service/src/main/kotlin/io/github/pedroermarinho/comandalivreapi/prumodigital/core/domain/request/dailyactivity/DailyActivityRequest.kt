package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "Formulário para a criação de uma nova atividade diária.")
data class DailyActivityRequest(
    @field:NotNull(message = "O ID do relatório diário é obrigatório.")
    @param:Schema(description = "ID público do relatório diário.")
    val dailyReportId: UUID,
    @field:NotBlank(message = "A descrição da atividade é obrigatória.")
    @param:Schema(description = "Descrição da atividade.")
    val description: String,
    @field:NotNull(message = "O ID do status da atividade é obrigatório.")
    @param:Schema(description = "ID público do status da atividade.")
    val statusId: UUID,
    @param:Schema(description = "Descrição da localização.")
    val location: String?,
    @param:Schema(description = "ID público do funcionário responsável.")
    val employeeId: UUID?,
)
