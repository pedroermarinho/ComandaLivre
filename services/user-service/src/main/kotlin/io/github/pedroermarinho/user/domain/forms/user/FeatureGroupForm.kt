package io.github.pedroermarinho.user.domain.forms.user

import java.util.*

data class FeatureGroupForm(
    val publicId: UUID?,
    val groupKey: String,
    val name: String,
    val description: String?,
)
