package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class ActivityAttachmentEntity(
    val id: EntityId,
    val dailyActivityId: Int,
    val assetId: Int,
    val description: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            dailyActivityId: Int,
            assetId: Int,
            description: String?,
        ): ActivityAttachmentEntity =
            ActivityAttachmentEntity(
                id = EntityId.createNew(publicId = publicId),
                dailyActivityId = dailyActivityId,
                assetId = assetId,
                description = description,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        assetId: Int,
        description: String?,
    ): ActivityAttachmentEntity =
        this.copy(
            assetId = assetId,
            description = description,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
