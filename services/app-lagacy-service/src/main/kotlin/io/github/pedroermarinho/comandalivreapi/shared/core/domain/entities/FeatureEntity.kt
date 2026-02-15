package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class FeatureEntity(
    val id: EntityId,
    val featureKey: String,
    val name: String,
    val description: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            featureKey: String,
            name: String,
            description: String?,
        ): FeatureEntity =
            FeatureEntity(
                id = EntityId.createNew(publicId = publicId),
                featureKey = featureKey,
                name = name,
                description = description,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        description: String?,
    ): FeatureEntity =
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
