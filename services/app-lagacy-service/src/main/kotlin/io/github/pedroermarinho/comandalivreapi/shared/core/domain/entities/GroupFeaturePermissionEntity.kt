package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import java.time.LocalDateTime

data class GroupFeaturePermissionEntity(
    val featureGroupId: Int,
    val featureId: Int,
    val isEnabled: Boolean,
    val grantedAt: LocalDateTime,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            featureGroupId: Int,
            featureId: Int,
            isEnabled: Boolean,
            grantedAt: LocalDateTime,
        ): GroupFeaturePermissionEntity =
            GroupFeaturePermissionEntity(
                featureGroupId = featureGroupId,
                featureId = featureId,
                isEnabled = isEnabled,
                grantedAt = grantedAt,
                audit = EntityAudit.createNew(),
            )
    }

    fun update(isEnabled: Boolean): GroupFeaturePermissionEntity =
        this.copy(
            isEnabled = isEnabled,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
