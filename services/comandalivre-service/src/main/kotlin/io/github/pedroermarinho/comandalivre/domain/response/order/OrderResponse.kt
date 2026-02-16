package io.github.pedroermarinho.comandalivre.domain.response.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de um pedido.")
data class OrderResponse(
    @param:Schema(description = "ID público do pedido.")
    val id: UUID,
    @param:Schema(description = "Detalhes do produto associado ao pedido.")
    val product: ProductResponse,
    @param:Schema(description = "Detalhes da comanda à qual o pedido pertence.")
    val command: CommandResponse,
    @param:Schema(description = "Status atual do pedido.")
    val status: OrderStatusResponse,
    @param:Schema(description = "Notas ou observações específicas do cliente para este pedido.")
    val notes: String?,
    @param:Schema(description = "Nível de prioridade do pedido (0 = normal, 1 = alta, 2 = crítica).")
    val priorityLevel: Int,
    @param:Schema(description = "Motivo do cancelamento do pedido, se aplicável.")
    val cancellationReason: String?,
    @param:Schema(description = "Preço unitário do item no momento em que o pedido foi feito.")
    val itemPriceAtOrder: BigDecimal?,
    @param:Schema(description = "Soma dos preços de todos os modificadores selecionados para este item.")
    val totalModifiersPrice: BigDecimal?,
    @param:Schema(description = "Data e hora de criação do pedido.")
    val createdAt: LocalDateTime,
)
