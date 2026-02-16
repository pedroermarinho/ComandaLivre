package io.github.pedroermarinho.prumodigital.domain.forms

import java.util.*

data class ActivityAttachmentForm(
    val dailyActivityId: UUID,
    val description: String?,
)
