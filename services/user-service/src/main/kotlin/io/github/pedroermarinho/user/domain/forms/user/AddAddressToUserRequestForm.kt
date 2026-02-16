package io.github.pedroermarinho.user.domain.forms.user

import io.github.pedroermarinho.user.domain.forms.address.AddressForm

data class AddAddressToUserRequestForm(
    val address: AddressForm,
    val nickname: String,
    val tag: String?,
)
