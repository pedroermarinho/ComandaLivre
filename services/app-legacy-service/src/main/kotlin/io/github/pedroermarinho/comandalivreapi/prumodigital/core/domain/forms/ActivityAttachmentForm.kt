package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms

import java.util.*

data class ActivityAttachmentForm(
    val dailyActivityId: UUID,
    val description: String?,
)
