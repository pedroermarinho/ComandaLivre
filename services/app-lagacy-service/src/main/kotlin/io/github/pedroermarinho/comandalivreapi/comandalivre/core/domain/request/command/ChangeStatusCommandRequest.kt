package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(
    name = "ChangeStatusCommandRequest",
    description = "Formulário para alterar o status de uma comanda.",
)
data class ChangeStatusCommandRequest(
    @field:NotNull(message = "O status da comanda é obrigatório.")
    @param:Schema(description = "Novo status para a comanda.", example = "CLOSED", required = true)
    val status: CommandStatusEnum,
    @param:Schema(description = "Indica se todas as ordens associadas devem ser finalizadas ao fechar a comanda. Padrão é falso.", defaultValue = "false")
    val closeAll: Boolean = false,
)
