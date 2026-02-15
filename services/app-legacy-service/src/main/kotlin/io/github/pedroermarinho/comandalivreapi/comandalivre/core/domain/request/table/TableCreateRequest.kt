package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "Formulário para a criação de uma nova mesa.")
data class TableCreateRequest(
    @param:Schema(description = "UUID público da mesa. Gerado automaticamente se não for fornecido.")
    val publicId: UUID? = null,
    @field:NotBlank(message = "O nome ou identificador da mesa é obrigatório.")
    @param:Schema(description = "Nome ou identificador da mesa.", example = "Mesa 10")
    val name: String,
    @field:Min(value = 1, message = "A mesa deve acomodar pelo menos uma pessoa.")
    @param:Schema(description = "Número de pessoas que a mesa suporta.", example = "4")
    val numPeople: Int,
    @param:Schema(description = "Descrição adicional ou localização da mesa.", example = "Perto da janela, com vista para o jardim.")
    val description: String?,
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa onde a mesa está localizada.", required = true)
    val companyId: UUID,
)
