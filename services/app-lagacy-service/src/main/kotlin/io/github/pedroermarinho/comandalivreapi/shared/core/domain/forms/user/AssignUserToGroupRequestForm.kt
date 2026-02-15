package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user

import java.time.LocalDateTime
import java.util.*

data class AssignUserToGroupRequestForm(
    val notes: String?,
    val expiresAt: LocalDateTime? = null,
)
