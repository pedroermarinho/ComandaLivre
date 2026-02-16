package io.github.pedroermarinho.company.domain.response.company

import java.time.LocalTime
import java.util.*

data class CompanySettingsResponse(
    val id: UUID,
    val logo: String?,
    val banner: String?,
    val primaryThemeColor: String?,
    val secondaryThemeColor: String?,
    val welcomeMessage: String?,
    val openTime: LocalTime?,
    val closeTime: LocalTime?,
    val isClosed: Boolean?,
    val domain: String?,
)
