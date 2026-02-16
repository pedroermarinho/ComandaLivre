package io.github.pedroermarinho.user.domain.forms.user

data class AddAddressToUserForm(
    val addressId: Int,
    val nickname: String,
    val tag: String?,
)
