package io.github.pedroermarinho.user.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import java.time.LocalDateTime

data class UserFeatureGroupEntity(
    val userId: Int,
    val featureGroupId: Int,
    val isActive: Boolean,
    val assignedAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val notes: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            userId: Int,
            featureGroupId: Int,
            isActive: Boolean,
            assignedAt: LocalDateTime,
            expiresAt: LocalDateTime?,
            notes: String?,
            createdBy: String? = null,
        ): UserFeatureGroupEntity =
            UserFeatureGroupEntity(
                userId = userId,
                featureGroupId = featureGroupId,
                isActive = isActive,
                assignedAt = assignedAt,
                expiresAt = expiresAt,
                notes = notes,
                audit = EntityAudit.createNew(),
            )
    }

    fun update(
        isActive: Boolean,
        expiresAt: LocalDateTime?,
        notes: String?,
    ): UserFeatureGroupEntity =
        this.copy(
            isActive = isActive,
            expiresAt = expiresAt,
            notes = notes,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
