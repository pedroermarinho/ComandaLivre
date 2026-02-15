package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(
    name = "UpdateTableReservationStatusRequest",
    description = "Formulário para atualizar o status de uma reserva de mesa.",
)
data class UpdateTableReservationStatusRequest(
    @field:NotBlank(message = "A chave de status é obrigatória.")
    @param:Schema(description = "Chave (key) do novo status da reserva.", example = "CANCELED", required = true)
    val statusKey: String,
)
