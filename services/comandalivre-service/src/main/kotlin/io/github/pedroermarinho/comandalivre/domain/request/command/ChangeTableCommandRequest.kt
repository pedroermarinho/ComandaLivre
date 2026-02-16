package io.github.pedroermarinho.comandalivre.domain.request.command

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(
    name = "ChangeTableCommandRequest",
    description = "Formulário para a troca de mesa de uma comanda.",
)
data class ChangeTableCommandRequest(
    @field:NotNull(message = "O ID da nova mesa é obrigatório.")
    @param:Schema(description = "ID público da nova mesa para a qual a comanda será movida.", required = true)
    val newTableId: UUID,
)
