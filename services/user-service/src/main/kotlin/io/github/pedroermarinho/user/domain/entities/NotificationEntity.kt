package io.github.pedroermarinho.user.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.OffsetDateTime
import java.util.*

data class NotificationEntity(
    val id: EntityId,
    val eventKey: String,
    val title: String,
    val message: String,
    val status: Boolean = false,
    val readAt: OffsetDateTime? = null,
    val action: Map<String, String>? = null,
    val userId: Int? = null,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            eventKey: String,
            title: String,
            message: String,
            status: Boolean = false,
            readAt: OffsetDateTime? = null,
            action: Map<String, String>? = null,
            userId: Int? = null,
        ): NotificationEntity =
            NotificationEntity(
                id = EntityId.createNew(publicId = publicId),
                eventKey = eventKey,
                title = title,
                message = message,
                status = status,
                readAt = readAt,
                action = action,
                userId = userId,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        status: Boolean,
        readAt: OffsetDateTime?,
        action: Map<String, String>?,
    ): NotificationEntity =
        this.copy(
            status = status,
            readAt = readAt,
            action = action,
            audit = this.audit.update(),
        )

    fun markAsRead(): NotificationEntity =
        this.copy(
            status = true,
            readAt = OffsetDateTime.now(),
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
