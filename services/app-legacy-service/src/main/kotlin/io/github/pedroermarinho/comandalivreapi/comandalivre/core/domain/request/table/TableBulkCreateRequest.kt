package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.table

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.UUID

@Schema(
    name = "TableBulkCreateRequest",
    description = "Formulário para a criação de múltiplas mesas em lote.",
)
data class TableBulkCreateRequest(
    @field:Positive(message = "O número inicial da sequência de mesas deve ser positivo.")
    @param:Schema(description = "Número inicial da sequência de mesas a serem criadas.", example = "1", required = true)
    val start: Int,
    @field:Positive(message = "O número final da sequência de mesas deve ser positivo.")
    @param:Schema(description = "Número final da sequência de mesas a serem criadas.", example = "10", required = true)
    val end: Int,
    @field:Min(value = 1, message = "Cada mesa deve acomodar pelo menos uma pessoa.")
    @param:Schema(description = "Capacidade padrão de pessoas para cada mesa criada.", example = "4", required = true)
    val numPeople: Int,
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa onde as mesas serão criadas.", required = true)
    val companyId: UUID,
)
