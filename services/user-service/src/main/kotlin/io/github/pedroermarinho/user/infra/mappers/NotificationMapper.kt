package io.github.pedroermarinho.user.infra.mappers

import io.github.pedroermarinho.user.domain.dtos.notification.NotificationDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserDTO
import io.github.pedroermarinho.user.domain.entities.NotificationEntity
import io.github.pedroermarinho.user.domain.response.notification.NotificationResponse
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import shared.tables.records.NotificationsRecord
import shared.tables.references.NOTIFICATIONS

@Component
class NotificationPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val dsl: DSLContext,
) {
    fun toRecord(entity: NotificationEntity): Result<NotificationsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrNull()
            dsl.newRecord(NOTIFICATIONS).apply {
                id = entity.id.internalId.let { if (it == 0) null else it }
                publicId = entity.id.publicId
                userId = entity.userId
                eventKey = entity.eventKey
                title = entity.title
                message = entity.message
                status = entity.status
                readAt = entity.readAt
                action = null
                createdAt = entity.audit.createdAt
                updatedAt = entity.audit.updatedAt
                deletedAt = entity.audit.deletedAt
                createdBy = entity.audit.createdBy ?: userAuth?.sub
                updatedBy = userAuth?.sub
                version = entity.audit.version
            }
        }

    fun toEntity(record: NotificationsRecord): Result<NotificationEntity> =
        errorDataConversion {
            NotificationEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                eventKey = record.eventKey,
                title = record.title,
                message = record.message,
                status = record.status!!,
                readAt = record.readAt,
                userId = record.userId,
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
class NotificationMapper {
    fun toDTO(
        entity: NotificationEntity,
        user: UserDTO?,
    ) = NotificationDTO(
        id = entity.id,
        eventKey = entity.eventKey,
        title = entity.title,
        message = entity.message,
        status = entity.status,
        readAt = entity.readAt,
        userId = user,
        createdAt = entity.audit.createdAt,
    )

    fun toResponse(dto: NotificationDTO) =
        NotificationResponse(
            id = dto.id.publicId,
            eventKey = dto.eventKey,
            title = dto.title,
            message = dto.message,
            status = dto.status,
            readAt = dto.readAt,
            createdAt = dto.createdAt,
        )
}
