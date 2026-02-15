package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user

import java.time.LocalDateTime

data class AssignUserToGroupForm(
    val userId: Int,
    val featureGroupId: Int,
    val notes: String?,
    val expiresAt: LocalDateTime? = null,
)
