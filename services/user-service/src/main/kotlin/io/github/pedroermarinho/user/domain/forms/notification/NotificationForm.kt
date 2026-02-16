package io.github.pedroermarinho.user.domain.forms.notification

import java.util.*

data class NotificationForm(
    val publicId: UUID? = null,
    val eventKey: String,
    val title: String,
    val message: String,
    val action: Map<String, String>? = null,
    val userId: Int?,
)
