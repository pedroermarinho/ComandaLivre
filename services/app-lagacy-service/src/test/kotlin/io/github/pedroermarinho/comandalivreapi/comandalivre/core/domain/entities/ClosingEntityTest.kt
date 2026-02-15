package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CashValue
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ClosingObservations
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para ClosingEntity")
class ClosingEntityTest {
    @Test
    @DisplayName("Deve criar ClosingEntity com todas as propriedades")
    fun shouldCreateClosingEntity() {
        val entityId = EntityId(internalId = 1, publicId = UUID.randomUUID())
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val closing =
            ClosingEntity(
                id = entityId,
                sessionId = 1,
                employeeId = 1,
                countedCash = CashValue(BigDecimal("100.00")),
                countedCard = CashValue(BigDecimal("200.00")),
                countedPix = CashValue(BigDecimal("50.00")),
                countedOthers = CashValue(BigDecimal("10.00")),
                finalBalance = CashValue(BigDecimal("360.00")),
                finalBalanceExpected = CashValue(BigDecimal("360.00")),
                finalBalanceDifference = CashValue(BigDecimal("0.00")),
                observations = ClosingObservations("No issues"),
                auditData = "{}",
                audit = audit,
            )

        assertEquals(entityId, closing.id)
        assertEquals(1, closing.sessionId)
        assertEquals(1, closing.employeeId)
        assertEquals(CashValue(BigDecimal("100.00")), closing.countedCash)
        assertEquals(CashValue(BigDecimal("200.00")), closing.countedCard)
        assertEquals(CashValue(BigDecimal("50.00")), closing.countedPix)
        assertEquals(CashValue(BigDecimal("10.00")), closing.countedOthers)
        assertEquals(CashValue(BigDecimal("360.00")), closing.finalBalance)
        assertEquals(CashValue(BigDecimal("360.00")), closing.finalBalanceExpected)
        assertEquals(CashValue(BigDecimal("0.00")), closing.finalBalanceDifference)
        assertEquals(ClosingObservations("No issues"), closing.observations)
        assertEquals("{}", closing.auditData)
        assertEquals(audit, closing.audit)
    }
}
