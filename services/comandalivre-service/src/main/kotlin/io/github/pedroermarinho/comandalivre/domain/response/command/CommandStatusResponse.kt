package io.github.pedroermarinho.comandalivre.domain.response.command

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação de um status de comanda.")
data class CommandStatusResponse(
    @param:Schema(description = "ID público do status.")
    val id: UUID,
    @param:Schema(description = "Chave textual única do status (ex: OPEN, CLOSED, CANCELED).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Aberta, Fechada, Cancelada).")
    val name: String,
    @param:Schema(description = "Descrição detalhada do status.")
    val description: String?,
)
