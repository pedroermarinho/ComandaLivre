package io.github.pedroermarinho.user.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class UserSettingsEntity(
    val id: EntityId,
    val userId: Int,
    val isDarkMode: Boolean,
    val selectedLanguage: String,
    val notificationsEnabled: Boolean,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            userId: Int,
            isDarkMode: Boolean,
            selectedLanguage: String,
            notificationsEnabled: Boolean,
        ): UserSettingsEntity =
            UserSettingsEntity(
                id = EntityId.createNew(publicId = publicId),
                userId = userId,
                isDarkMode = isDarkMode,
                selectedLanguage = selectedLanguage,
                notificationsEnabled = notificationsEnabled,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        isDarkMode: Boolean,
        selectedLanguage: String,
        notificationsEnabled: Boolean,
    ): UserSettingsEntity =
        this.copy(
            isDarkMode = isDarkMode,
            selectedLanguage = selectedLanguage,
            notificationsEnabled = notificationsEnabled,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
