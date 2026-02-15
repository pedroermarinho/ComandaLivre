package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CashValue
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderNotes
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserId
import java.math.BigDecimal
import java.time.LocalDateTime

data class SessionEntity(
    val id: EntityId,
    val companyId: CompanyId,
    val employeeId: EmployeeId,
    val openedByUserId: UserId?,
    val closedByUserId: UserId? = null,
    val initialValue: CashValue,
    val status: SessionStatus,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime? = null,
    val notes: OrderNotes?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            initialValue: BigDecimal,
            notes: String?,
            employeeId: Int,
            companyId: Int,
            openedByUserId: Int,
            status: SessionStatus,
        ): SessionEntity =
            SessionEntity(
                id = EntityId.createNew(),
                companyId = CompanyId(companyId),
                employeeId = EmployeeId(employeeId),
                openedByUserId = UserId(openedByUserId),
                initialValue = CashValue(initialValue),
                notes = notes?.let { OrderNotes(it) },
                status = status,
                startedAt = LocalDateTime.now(),
                audit = EntityAudit.createNew(),
            )
    }

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )

    fun updateStatus(
        status: SessionStatus,
        closedByUserId: Int,
    ) = this.copy(
        status = status,
        closedByUserId = UserId(closedByUserId),
        audit = this.audit.update(),
    )
}
