package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class UserSettingsDTO(
    val id: EntityId,
    val userId: Int,
    val isDarkMode: Boolean,
    val selectedLanguage: String,
    val notificationsEnabled: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
