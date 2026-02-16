package io.github.pedroermarinho.user.domain.forms.user

import java.time.LocalDateTime
import java.util.*

data class AssignUserToGroupRequestForm(
    val notes: String?,
    val expiresAt: LocalDateTime? = null,
)
