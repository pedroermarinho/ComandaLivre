package io.github.pedroermarinho.comandalivre.domain.response.cashregister

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeResponse
import io.github.pedroermarinho.user.domain.response.user.UserResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de uma sessão de caixa.")
data class SessionResponse(
    @param:Schema(description = "ID público da sessão de caixa.")
    val id: UUID,
    @param:Schema(description = "Detalhes do funcionário responsável pela sessão.")
    val employee: EmployeeResponse,
    @param:Schema(description = "Detalhes do usuário que abriu a sessão.")
    val openedByUser: UserResponse?,
    @param:Schema(description = "Detalhes do usuário que fechou a sessão.")
    val closedByUser: UserResponse?,
    @param:Schema(description = "Valor inicial do caixa ao abrir a sessão.")
    val initialValue: BigDecimal,
    @param:Schema(description = "Status atual da sessão de caixa.")
    val status: CashRegisterSessionStatusResponse,
    @param:Schema(description = "Data e hora de abertura da sessão.")
    val startedAt: LocalDateTime,
    @param:Schema(description = "Data e hora de fechamento da sessão.")
    val endedAt: LocalDateTime?,
    @param:Schema(description = "Observações gerais sobre a sessão de caixa.")
    val notes: String?,
    @param:Schema(description = "Data e hora de criação do registro da sessão.")
    val createdAt: LocalDateTime,
)
