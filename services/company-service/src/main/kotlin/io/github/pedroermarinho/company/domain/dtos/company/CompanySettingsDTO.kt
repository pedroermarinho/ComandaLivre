package io.github.pedroermarinho.company.domain.dtos.company

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalTime
import java.util.*

data class CompanySettingsDTO(
    val id: EntityId,
    val logo: String?,
    val banner: String?,
    val primaryThemeColor: String?,
    val secondaryThemeColor: String?,
    val welcomeMessage: String?,
    val timezone: String?,
    val openTime: LocalTime?,
    val closeTime: LocalTime?,
    val isClosed: Boolean?,
    val notificationEmails: List<String>?,
    val domain: String?,
)
