package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.UUID

data class VersionEntity(
    val id: EntityId,
    val version: String,
    val platform: String,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            version: String,
            platform: String,
            createdBy: String? = null,
        ): VersionEntity =
            VersionEntity(
                id = EntityId.createNew(publicId = publicId),
                version = version,
                platform = platform,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
