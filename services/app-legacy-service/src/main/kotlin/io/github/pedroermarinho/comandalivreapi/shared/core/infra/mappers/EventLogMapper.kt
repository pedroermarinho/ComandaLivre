package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.eventlog.EventLogDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.EventLogEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.eventlog.EventLogResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.EventLogRecord

@Component
class EventLogPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: EventLogEntity): Result<EventLogRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrNull()
            EventLogRecord(
                id = entity.id.internalId.let { if (it == 0) null else it.toLong() },
                publicId = entity.id.publicId,
                eventKey = entity.eventKey,
                eventTitle = entity.eventTitle,
                eventDescription = entity.eventDescription,
                actorUserSub = entity.actorUserSub,
                targetEntityType = entity.targetEntityType,
                targetEntityKey = entity.targetEntityKey,
                eventData = null,
                tags = entity.tags?.toTypedArray(),
                ipAddress = entity.ipAddress,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth?.sub,
                updatedBy = userAuth?.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: EventLogRecord): Result<EventLogEntity> =
        errorDataConversion {
            EventLogEntity(
                id =
                    EntityId(
                        internalId = record.id!!.toInt(),
                        publicId = record.publicId,
                    ),
                eventKey = record.eventKey,
                eventTitle = record.eventTitle,
                eventDescription = record.eventDescription,
                actorUserSub = record.actorUserSub,
                targetEntityType = record.targetEntityType,
                targetEntityKey = record.targetEntityKey,
                tags = record.tags?.toList(),
                ipAddress = record.ipAddress,
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
class EventLogMapper {
    fun toDTO(entity: EventLogEntity): EventLogDTO =
        EventLogDTO(
            id = entity.id,
            eventKey = entity.eventKey,
            eventTitle = entity.eventTitle,
            eventDescription = entity.eventDescription,
            actorUserSub = entity.actorUserSub,
            targetEntityType = entity.targetEntityType,
            targetEntityKey = entity.targetEntityKey,
            tags = entity.tags,
            ipAddress = entity.ipAddress,
            loggedAt = entity.audit.createdAt,
        )

    fun toResponse(dto: EventLogDTO) =
        EventLogResponse(
            id = dto.id.publicId,
            eventKey = dto.eventKey,
            eventTitle = dto.eventTitle,
            eventDescription = dto.eventDescription,
            actorUserSub = dto.actorUserSub,
            targetEntityType = dto.targetEntityType,
            targetEntityKey = dto.targetEntityKey,
            tags = dto.tags,
            ipAddress = dto.ipAddress,
            loggedAt = dto.loggedAt,
        )
}
