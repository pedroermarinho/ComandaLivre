package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.CashRegisterClosingsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ClosingEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CashValue
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ClosingObservations
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class ClosingPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ClosingEntity): Result<CashRegisterClosingsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            CashRegisterClosingsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                sessionId = entity.sessionId,
                employeeId = entity.employeeId,
                countedCash = entity.countedCash.value,
                countedCard = entity.countedCard.value,
                countedPix = entity.countedPix.value,
                countedOthers = entity.countedOthers.value,
                finalBalance = entity.finalBalance.value,
                finalBalanceExpected = entity.finalBalanceExpected.value,
                finalBalanceDifference = entity.finalBalanceDifference.value,
                observations = entity.observations?.value,
                auditData = entity.auditData?.let { org.jooq.JSONB.valueOf(it) },
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: CashRegisterClosingsRecord): Result<ClosingEntity> =
        errorDataConversion {
            ClosingEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                sessionId = record.sessionId,
                employeeId = record.employeeId,
                countedCash = CashValue.restore(record.countedCash!!),
                countedCard = CashValue.restore(record.countedCard!!),
                countedPix = CashValue.restore(record.countedPix!!),
                countedOthers = CashValue.restore(record.countedOthers!!),
                finalBalance = CashValue.restore(record.finalBalance),
                finalBalanceExpected = CashValue.restore(record.finalBalanceExpected),
                finalBalanceDifference = CashValue.restore(record.finalBalanceDifference),
                observations = record.observations?.let { ClosingObservations.restore(it) },
                auditData = record.auditData?.data(),
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}
