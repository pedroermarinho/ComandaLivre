package io.github.pedroermarinho.company.domain.forms.company

import io.github.pedroermarinho.comandalivreapi.company.core.domain.validation.UniqueCompanyDomain
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalTime

class CompanySettingsForm(
    @field:Size(max = 7)
    val primaryThemeColor: String? = null,
    @field:Size(max = 7)
    val secondaryThemeColor: String? = null,
    @field:Size(max = 1000)
    val welcomeMessage: String? = null,
    val timezone: String? = null,
    val openTime: LocalTime? = null,
    val closeTime: LocalTime? = null,
    @field:Size(max = 255)
    @field:Pattern(
        regexp = "^[a-zA-Z0-9-_]+$",
        message = "O domínio deve conter apenas letras, números, hífens e sublinhados.",
    )
    @UniqueCompanyDomain
    val domain: String? = null,
)
