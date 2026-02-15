package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import comandalivre.tables.records.TableReservationStatusRecord
import comandalivre.tables.records.TableReservationsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableReservationEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableReservationStatus
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class TableReservationStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: TableReservationStatus): Result<TableReservationStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            TableReservationStatusRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key.value,
                name = entity.name.value,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: TableReservationStatusRecord): Result<TableReservationStatus> =
        errorDataConversion {
            TableReservationStatus(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = TypeKey.restore(record.key),
                name = TypeName.restore(record.name),
                description = record.description,
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

@Component
class TableReservationPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val tableReservationStatusMapper: TableReservationStatusPersistenceMapper,
) {
    fun toRecord(entity: TableReservationEntity): Result<TableReservationsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            TableReservationsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                tableId = entity.tableId.value,
                reservedFor = entity.reservedFor,
                reservedForUserId = entity.reservedForUserId?.value,
                reservationStart = entity.reservationStart,
                reservationEnd = entity.reservationEnd,
                statusId = entity.status.id.internalId,
                notes = entity.notes,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        tableReservationsRecord: TableReservationsRecord,
        tableReservationsStatusRecord: TableReservationStatusRecord,
    ): Result<TableReservationEntity> =
        errorDataConversion {
            TableReservationEntity(
                id =
                    EntityId(
                        internalId = tableReservationsRecord.id!!,
                        publicId = tableReservationsRecord.publicId,
                    ),
                tableId = TableId.restore(tableReservationsRecord.tableId),
                reservedFor = tableReservationsRecord.reservedFor,
                reservedForUserId = tableReservationsRecord.reservedForUserId?.let { UserId.restore(it) },
                reservationStart = tableReservationsRecord.reservationStart,
                reservationEnd = tableReservationsRecord.reservationEnd,
                status = tableReservationStatusMapper.toEntity(tableReservationsStatusRecord).getOrThrow(),
                notes = tableReservationsRecord.notes,
                audit =
                    EntityAudit(
                        createdAt = tableReservationsRecord.createdAt!!,
                        updatedAt = tableReservationsRecord.updatedAt!!,
                        deletedAt = tableReservationsRecord.deletedAt,
                        createdBy = tableReservationsRecord.createdBy,
                        updatedBy = tableReservationsRecord.updatedBy,
                        version = tableReservationsRecord.version!!,
                    ),
            )
        }
}
