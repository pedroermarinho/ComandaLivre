package io.github.pedroermarinho.comandalivre.domain.request.cashregister

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

@Schema(
    name = "StartSessionRequest",
    description = "Formulário para a abertura de uma nova sessão de caixa.",
)
data class StartSessionRequest(
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa para a qual a sessão de caixa será iniciada.", required = true)
    val companyId: UUID,
    @field:NotNull(message = "O valor inicial é obrigatório.")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "O valor inicial não pode ser negativo")
    @param:Schema(description = "Valor inicial de troco no caixa.", example = "100.00", required = true)
    val initialValue: BigDecimal,
    @param:Schema(description = "Observações ou notas sobre a abertura do caixa.", example = "Iniciando o turno da manhã.")
    val notes: String?,
)
