package io.github.pedroermarinho.comandalivre.domain.response.cashregister

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de um fechamento de caixa.")
data class ClosingResponse(
    @param:Schema(description = "ID público do fechamento de caixa.")
    val id: UUID,
    @param:Schema(description = "Detalhes da sessão de caixa que foi fechada.")
    val session: SessionResponse,
    @param:Schema(description = "Detalhes do funcionário que realizou o fechamento.")
    val employee: EmployeeResponse,
    @param:Schema(description = "Valor em dinheiro contado no fechamento.")
    val countedCash: BigDecimal,
    @param:Schema(description = "Valor em cartão contado no fechamento.")
    val countedCard: BigDecimal,
    @param:Schema(description = "Valor em PIX contado no fechamento.")
    val countedPix: BigDecimal,
    @param:Schema(description = "Outros valores contados no fechamento.")
    val countedOthers: BigDecimal,
    @param:Schema(description = "Saldo final total do caixa após o fechamento.")
    val finalBalance: BigDecimal,
    @param:Schema(description = "Saldo final esperado do caixa com base nas transações.")
    val finalBalanceExpected: BigDecimal,
    @param:Schema(description = "Diferença entre o saldo final contado e o esperado.")
    val finalBalanceDifference: BigDecimal,
    @param:Schema(description = "Observações registradas durante o fechamento.")
    val observations: String?,
    @param:Schema(description = "Dados de auditoria do fechamento.")
    val auditData: String?,
    @param:Schema(description = "Data e hora de criação do registro de fechamento.")
    val createdAt: LocalDateTime,
)
