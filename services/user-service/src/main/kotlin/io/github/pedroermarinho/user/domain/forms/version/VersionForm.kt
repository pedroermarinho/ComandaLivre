package io.github.pedroermarinho.user.domain.forms.version

import io.github.pedroermarinho.user.domain.enums.PlatformEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class VersionForm(
    @field:NotBlank
    val version: String,
    @field:NotNull
    val platform: PlatformEnum,
)
