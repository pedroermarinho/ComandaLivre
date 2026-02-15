package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CashValue
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderNotes
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para SessionEntity")
class SessionEntityTest {
    @Test
    @DisplayName("Deve criar SessionEntity com todas as propriedades")
    fun shouldCreateSessionEntity() {
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
        val sessionStatus =
            SessionStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Session is open",
                audit = audit,
            )
        val session =
            SessionEntity(
                id = entityId,
                companyId = CompanyId(1),
                employeeId = EmployeeId(1),
                openedByUserId = UserId(1),
                closedByUserId = null,
                initialValue = CashValue(BigDecimal("100.00")),
                status = sessionStatus,
                startedAt = LocalDateTime.now(),
                endedAt = null,
                notes = OrderNotes("Opening session"),
                audit = audit,
            )

        assertEquals(entityId, session.id)
        assertEquals(CompanyId(1), session.companyId)
        assertEquals(EmployeeId(1), session.employeeId)
        assertEquals(UserId(1), session.openedByUserId)
        assertNull(session.closedByUserId)
        assertEquals(CashValue(BigDecimal("100.00")), session.initialValue)
        assertEquals(sessionStatus, session.status)
        assertNotNull(session.startedAt)
        assertNull(session.endedAt)
        assertEquals(OrderNotes("Opening session"), session.notes)
        assertEquals(audit, session.audit)
    }
}
