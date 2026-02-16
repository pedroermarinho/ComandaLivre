package io.github.pedroermarinho.prumodigital.domain.response.project

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação de um status de projeto.")
data class ProjectStatusResponse(
    @param:Schema(description = "ID público do status.")
    val id: UUID,
    @param:Schema(description = "Chave textual única do status (ex: PLANNING, IN_PROGRESS, COMPLETED).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Em Planejamento, Em Andamento, Concluído).")
    val name: String,
    @param:Schema(description = "Descrição detalhada do status.")
    val description: String?,
    @param:Schema(description = "Data e hora de criação do status.")
    val createdAt: LocalDateTime,
)
