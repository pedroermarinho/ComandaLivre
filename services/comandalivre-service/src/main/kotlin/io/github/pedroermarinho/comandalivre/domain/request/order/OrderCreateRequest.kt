package io.github.pedroermarinho.comandalivre.domain.request.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.order.OrderItemsCreateForm
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(
    name = "OrderCreateRequest",
    description = "Formulário para a criação de um novo pedido com múltiplos itens.",
)
data class OrderCreateRequest(
    @field:NotNull(message = "O ID da comanda é obrigatório.")
    @param:Schema(description = "ID público da comanda à qual o pedido será associado.", required = true)
    val commandId: UUID,
    @field:NotEmpty(message = "A lista de itens não pode ser vazia.")
    @param:Schema(description = "Lista de itens que compõem o pedido.", required = true)
    @field:Valid
    val items: List<OrderItemsCreateForm>,
)
