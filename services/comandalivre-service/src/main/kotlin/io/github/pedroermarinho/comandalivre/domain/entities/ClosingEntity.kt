package io.github.pedroermarinho.comandalivre.domain.entities

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CashValue
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ClosingObservations
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal

data class ClosingEntity(
    val id: EntityId,
    val sessionId: Int,
    val employeeId: Int,
    val countedCash: CashValue,
    val countedCard: CashValue,
    val countedPix: CashValue,
    val countedOthers: CashValue,
    val finalBalance: CashValue,
    val finalBalanceExpected: CashValue,
    val finalBalanceDifference: CashValue,
    val observations: ClosingObservations?,
    val auditData: String?,
    val audit: EntityAudit,
) {
    companion object {
        private val log = KotlinLogging.logger {}

        fun createNew(
            sessionId: Int,
            employeeId: Int,
            countedCash: BigDecimal,
            countedCard: BigDecimal,
            countedPix: BigDecimal,
            countedOthers: BigDecimal,
            observations: String?,
            initialValue: CashValue,
            commands: List<CommandDTO>,
        ): ClosingEntity {
            val finalBalance =
                initialValue.value
                    .plus(countedCash)
                    .plus(countedCard)
                    .plus(countedPix)
                    .plus(countedOthers)

            val finalBalanceExpected =
                initialValue.value.plus(
                    commands
                        .filter { it.totalAmount != null }
                        .map { it.totalAmount!! }
                        .sumOf { it },
                )

            val finalBalanceDifference = finalBalance.minus(finalBalanceExpected)

            log.info {
                "Finalizando sessão ID: $sessionId, valor final contado: $finalBalance, valor final esperado: $finalBalanceExpected, diferença: $finalBalanceDifference"
            }

            return ClosingEntity(
                id = EntityId.createNew(),
                sessionId = sessionId,
                employeeId = employeeId,
                countedCash = CashValue(countedCash),
                countedCard = CashValue(countedCard),
                countedPix = CashValue(countedPix),
                countedOthers = CashValue(countedOthers),
                finalBalance = CashValue(finalBalance),
                finalBalanceExpected = CashValue(finalBalanceExpected),
                finalBalanceDifference = CashValue(finalBalanceDifference),
                observations = observations?.let { ClosingObservations(it) },
                auditData = null,
                audit = EntityAudit.createNew(),
            )
        }
    }

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
