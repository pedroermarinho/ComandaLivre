package io.github.pedroermarinho.comandalivre.domain.response.cashregister

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Relatório detalhado de fechamento de caixa.")
data class CashRegisterClosingReportResponse(
    @param:Schema(description = "ID público do relatório de fechamento.")
    val id: UUID,
    @param:Schema(description = "ID público da sessão de caixa relacionada.")
    val sessionId: UUID,
    @param:Schema(description = "ID público do funcionário que realizou o fechamento.")
    val employeeId: UUID,
    @param:Schema(description = "Valor esperado em dinheiro.")
    val expectedCash: BigDecimal,
    @param:Schema(description = "Valor esperado em cartão.")
    val expectedCard: BigDecimal,
    @param:Schema(description = "Valor esperado em PIX.")
    val expectedPix: BigDecimal,
    @param:Schema(description = "Valor esperado em outras formas de pagamento.")
    val expectedOthers: BigDecimal,
    @param:Schema(description = "Valor contado em dinheiro.")
    val countedCash: BigDecimal,
    @param:Schema(description = "Valor contado em cartão.")
    val countedCard: BigDecimal,
    @param:Schema(description = "Valor contado em PIX.")
    val countedPix: BigDecimal,
    @param:Schema(description = "Valor contado em outras formas de pagamento.")
    val countedOthers: BigDecimal,
    @param:Schema(description = "Diferença entre o esperado e o contado para dinheiro.")
    val cashDiscrepancy: BigDecimal,
    @param:Schema(description = "Diferença entre o esperado e o contado para cartão.")
    val cardDiscrepancy: BigDecimal,
    @param:Schema(description = "Diferença entre o esperado e o contado para PIX.")
    val pixDiscrepancy: BigDecimal,
    @param:Schema(description = "Diferença entre o esperado e o contado para outras formas de pagamento.")
    val othersDiscrepancy: BigDecimal,
    @param:Schema(description = "Saldo final total do caixa.")
    val finalBalance: BigDecimal,
    @param:Schema(description = "Observações registradas durante o fechamento.")
    val observations: String?,
    @param:Schema(description = "Dados de auditoria.")
    val auditData: String?,
    @param:Schema(description = "Data e hora de criação do relatório.")
    val createdAt: LocalDateTime,
)
