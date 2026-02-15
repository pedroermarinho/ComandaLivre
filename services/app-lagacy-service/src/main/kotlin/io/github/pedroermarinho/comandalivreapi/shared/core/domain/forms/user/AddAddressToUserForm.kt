package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user

data class AddAddressToUserForm(
    val addressId: Int,
    val nickname: String,
    val tag: String?,
)
