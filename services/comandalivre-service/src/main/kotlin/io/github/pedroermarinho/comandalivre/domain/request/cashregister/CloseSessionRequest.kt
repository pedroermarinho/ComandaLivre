package io.github.pedroermarinho.comandalivre.domain.request.cashregister

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

@Schema(
    name = "CloseSessionRequest",
    description = "Formulário para o fechamento de uma sessão de caixa.",
)
data class CloseSessionRequest(
    @field:NotNull(message = "O valor em dinheiro contado é obrigatório.")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "O valor em dinheiro não pode ser negativo.")
    @param:Schema(description = "Valor total em dinheiro contado no fechamento.", example = "150.75")
    val countedCash: BigDecimal,
    @field:NotNull(message = "O valor em cartão contado é obrigatório.")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "O valor em cartão não pode ser negativo.")
    @param:Schema(description = "Valor total em cartão contado no fechamento.", example = "320.50")
    val countedCard: BigDecimal,
    @field:NotNull(message = "O valor em PIX contado é obrigatório.")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "O valor em PIX não pode ser negativo.")
    @param:Schema(description = "Valor total em PIX contado no fechamento.", example = "210.00")
    val countedPix: BigDecimal,
    @field:NotNull(message = "O valor de 'outros' contado é obrigatório.")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "O valor de 'outros' não pode ser negativo.")
    @param:Schema(description = "Valor total de outras formas de pagamento contado no fechamento.", example = "50.00")
    val countedOthers: BigDecimal,
    @param:Schema(description = "Observações adicionais sobre o fechamento do caixa.", example = "Diferença de R$2,00 no caixa.")
    val observations: String?,
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa para a qual a sessão de caixa será fechada.", required = true)
    val companyId: UUID,
)
