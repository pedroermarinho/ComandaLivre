package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.CashRegisterSessionStatusRecord
import comandalivre.tables.records.CashRegisterSessionsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.SessionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CashValue
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderNotes
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeId
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.UserId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class SessionPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val sessionStatusPersistenceMapper: SessionStatusPersistenceMapper,
) {
    fun toRecord(entity: SessionEntity): Result<CashRegisterSessionsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            CashRegisterSessionsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                companyId = entity.companyId.value,
                employeeId = entity.employeeId.value,
                openedByUserId = entity.openedByUserId?.value,
                closedByUserId = entity.closedByUserId?.value,
                initialValue = entity.initialValue.value,
                statusId = entity.status.id.internalId,
                startedAt = entity.startedAt,
                endedAt = entity.endedAt,
                notes = entity.notes?.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        cashRegisterSessionsRecord: CashRegisterSessionsRecord,
        cashRegisterSessionStatusRecord: CashRegisterSessionStatusRecord,
    ): Result<SessionEntity> =
        errorDataConversion {
            SessionEntity(
                id =
                    EntityId(
                        internalId = cashRegisterSessionsRecord.id!!,
                        publicId = cashRegisterSessionsRecord.publicId,
                    ),
                companyId = CompanyId.restore(cashRegisterSessionsRecord.companyId),
                employeeId = EmployeeId.restore(cashRegisterSessionsRecord.employeeId),
                openedByUserId = cashRegisterSessionsRecord.openedByUserId?.let { UserId.restore(it) },
                closedByUserId = cashRegisterSessionsRecord.closedByUserId?.let { UserId.restore(it) },
                initialValue = CashValue.restore(cashRegisterSessionsRecord.initialValue),
                status = sessionStatusPersistenceMapper.toEntity(cashRegisterSessionStatusRecord).getOrThrow(),
                startedAt = cashRegisterSessionsRecord.startedAt!!,
                endedAt = cashRegisterSessionsRecord.endedAt,
                notes = cashRegisterSessionsRecord.notes?.let { OrderNotes.restore(it) },
                audit =
                    EntityAudit(
                        createdAt = cashRegisterSessionsRecord.createdAt!!,
                        updatedAt = cashRegisterSessionsRecord.updatedAt!!,
                        deletedAt = cashRegisterSessionsRecord.deletedAt,
                        createdBy = cashRegisterSessionsRecord.createdBy,
                        updatedBy = cashRegisterSessionsRecord.updatedBy,
                        version = cashRegisterSessionsRecord.version!!,
                    ),
            )
        }
}
