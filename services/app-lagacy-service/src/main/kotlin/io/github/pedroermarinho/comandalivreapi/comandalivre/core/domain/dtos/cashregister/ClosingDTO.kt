package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDateTime

data class ClosingDTO(
    val id: EntityId,
    val session: SessionDTO,
    val employee: EmployeeDTO,
    val countedCash: BigDecimal,
    val countedCard: BigDecimal,
    val countedPix: BigDecimal,
    val countedOthers: BigDecimal,
    val finalBalance: BigDecimal,
    val finalBalanceExpected: BigDecimal,
    val finalBalanceDifference: BigDecimal,
    val observations: String?,
    val auditData: String?,
    val createdAt: LocalDateTime,
)
