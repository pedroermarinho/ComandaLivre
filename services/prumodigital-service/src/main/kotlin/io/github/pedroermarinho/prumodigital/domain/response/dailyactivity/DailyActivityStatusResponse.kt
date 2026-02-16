package io.github.pedroermarinho.prumodigital.domain.response.dailyactivity

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação de um status de atividade diária.")
data class DailyActivityStatusResponse(
    @param:Schema(description = "ID público do status.")
    val id: UUID,
    @param:Schema(description = "Chave textual única do status (ex: PLANNED, IN_PROGRESS, COMPLETED).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Planejada, Em Andamento, Concluída).")
    val name: String,
    @param:Schema(description = "Descrição detalhada do status.")
    val description: String?,
    @param:Schema(description = "Data e hora de criação do status.")
    val createdAt: LocalDateTime,
)
