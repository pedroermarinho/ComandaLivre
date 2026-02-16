package io.github.pedroermarinho.comandalivre.domain.request.order

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(
    name = "ChangeStatusOrderRequest",
    description = "Formulário para alterar o status de um pedido.",
)
data class ChangeStatusOrderRequest(
    @field:NotBlank(message = "O status do pedido é obrigatório.")
    @param:Schema(description = "Chave (key) do novo status para o pedido.", example = "DELIVERED", required = true)
    val status: String,
)
