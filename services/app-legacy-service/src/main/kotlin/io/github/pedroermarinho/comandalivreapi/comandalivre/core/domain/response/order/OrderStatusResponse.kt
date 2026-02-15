package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.order

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação de um status de pedido.")
data class OrderStatusResponse(
    @param:Schema(description = "ID público do status.")
    val id: UUID,
    @param:Schema(description = "Chave textual única do status (ex: PENDING, PREPARING, DELIVERED).")
    val key: String,
    @param:Schema(description = "Nome legível do status (ex: Pendente, Em Preparo, Entregue).")
    val name: String,
    @param:Schema(description = "Descrição detalhada do status.")
    val description: String?,
)
