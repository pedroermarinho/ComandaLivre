package io.github.pedroermarinho.user.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class FeatureFlagEntity(
    val id: EntityId,
    val name: String,
    val description: String?,
    val keyFlag: String,
    val enabled: Boolean,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            name: String,
            description: String?,
            keyFlag: String,
            enabled: Boolean,
        ): FeatureFlagEntity =
            FeatureFlagEntity(
                id = EntityId.createNew(publicId = publicId),
                name = name,
                description = description,
                keyFlag = keyFlag,
                enabled = enabled,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        description: String?,
        enabled: Boolean,
    ): FeatureFlagEntity =
        this.copy(
            name = name,
            description = description,
            enabled = enabled,
            audit = this.audit.update(),
        )

    fun updateEnabled(enabled: Boolean): FeatureFlagEntity =
        this.copy(
            enabled = enabled,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
