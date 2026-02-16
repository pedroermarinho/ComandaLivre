package io.github.pedroermarinho.comandalivre.domain.response.cashregister

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação do status de uma sessão de caixa.")
data class CashRegisterSessionStatusResponse(
    @param:Schema(description = "ID público do status.")
    val id: UUID,
    @param:Schema(description = "Chave textual do status (ex: OPEN, CLOSED).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Aberta, Fechada).")
    val name: String,
    @param:Schema(description = "Descrição do status.")
    val description: String?,
)
