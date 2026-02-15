package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação de um status de mesa.")
data class TableStatusResponse(
    @param:Schema(description = "ID público do status.")
    val id: UUID,
    @param:Schema(description = "Chave textual única do status (ex: AVAILABLE, OCCUPIED, RESERVED).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Disponível, Ocupada, Reservada).")
    val name: String,
    @param:Schema(description = "Descrição detalhada do status.")
    val description: String?,
)
