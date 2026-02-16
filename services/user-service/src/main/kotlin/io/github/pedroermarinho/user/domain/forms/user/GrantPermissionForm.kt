package io.github.pedroermarinho.user.domain.forms.user

data class GrantPermissionForm(
    val groupId: Int,
    val featureId: Int,
)
