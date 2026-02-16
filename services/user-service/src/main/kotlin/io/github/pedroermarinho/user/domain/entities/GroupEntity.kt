package io.github.pedroermarinho.user.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class GroupEntity(
    val id: EntityId,
    val groupKey: String,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            groupKey: String,
            name: String,
            description: String?,
            createdAt: LocalDateTime,
        ): GroupEntity =
            GroupEntity(
                id = EntityId.createNew(publicId = publicId),
                groupKey = groupKey,
                name = name,
                description = description,
                createdAt = createdAt,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        description: String?,
    ): GroupEntity =
        this.copy(
            name = name,
            description = description,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
