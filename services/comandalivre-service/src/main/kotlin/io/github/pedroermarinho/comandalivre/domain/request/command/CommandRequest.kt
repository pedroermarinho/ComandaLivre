package io.github.pedroermarinho.comandalivre.domain.request.command

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.UUID

@Schema(description = "Formulário para criação ou atualização de comandas.")
data class CommandRequestForm(
    @param:Schema(description = "UUID público da comanda.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val publicId: UUID? = null,
    @param:Schema(description = "Nome da comanda.", example = "Mesa 1", required = true)
    @field:NotNull(message = "O nome da comanda é obrigatório.")
    val name: String,
    @field:NotNull(message = "O ID do funcionário é obrigatório.")
    @param:Schema(
        description = "ID do funcionário responsável pela comanda.",
        example = "d290f1ee-6c54-4b01-90e6-d701748f0851",
        required = true,
    )
    val employeeId: UUID,
    @field:NotNull(message = "O número de pessoas é obrigatório.")
    @field:Positive(message = "O número de pessoas deve ser maior que zero.")
    @param:Schema(description = "Número de pessoas na comanda.", example = "4", required = true)
    val numberOfPeople: Int,
    @field:NotNull(message = "A mesa é obrigatória.")
    @param:Schema(
        description = "ID da mesa associada à comanda.",
        example = "d290f1ee-6c54-4b01-90e6-d701748f0851",
        required = true,
    )
    val tableId: UUID,
    @param:Schema(description = "ID do usuário associado à comanda.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val userId: UUID? = null,
)
