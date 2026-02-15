package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de uma mesa.")
data class TableResponse(
    @param:Schema(description = "ID público da mesa.")
    val id: UUID,
    @param:Schema(description = "Nome ou identificador da mesa.")
    val name: String,
    @param:Schema(description = "Número de pessoas que a mesa suporta.")
    val numPeople: Int,
    @param:Schema(description = "Status atual da mesa.")
    val status: TableStatusResponse,
    @param:Schema(description = "Descrição adicional ou localização da mesa.")
    val description: String?,
    @param:Schema(description = "Data e hora de criação da mesa.")
    val createdAt: LocalDateTime,
    @param:Schema(description = "Data e hora da última atualização da mesa.")
    val updatedAt: LocalDateTime,
)
