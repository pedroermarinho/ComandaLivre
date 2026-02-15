package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user

data class GrantPermissionForm(
    val groupId: Int,
    val featureId: Int,
)
