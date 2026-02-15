package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class EventLogEntity(
    val id: EntityId,
    val eventKey: String,
    val eventTitle: String,
    val eventDescription: String?,
    val actorUserSub: String?,
    val targetEntityType: String?,
    val targetEntityKey: String?,
    val tags: List<String?>?,
    val ipAddress: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            eventKey: String,
            eventTitle: String,
            eventDescription: String?,
            actorUserSub: String?,
            targetEntityType: String?,
            targetEntityKey: String?,
            tags: List<String?>?,
            ipAddress: String?,
        ): EventLogEntity =
            EventLogEntity(
                id = EntityId.createNew(publicId = publicId),
                eventKey = eventKey,
                eventTitle = eventTitle,
                eventDescription = eventDescription,
                actorUserSub = actorUserSub,
                targetEntityType = targetEntityType,
                targetEntityKey = targetEntityKey,
                tags = tags,
                ipAddress = ipAddress,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        eventDescription: String?,
        tags: List<String?>?,
    ): EventLogEntity =
        this.copy(
            eventDescription = eventDescription,
            tags = tags,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
