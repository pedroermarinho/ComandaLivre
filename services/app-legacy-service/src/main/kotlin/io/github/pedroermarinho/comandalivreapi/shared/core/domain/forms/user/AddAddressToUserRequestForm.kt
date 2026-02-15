package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm

data class AddAddressToUserRequestForm(
    val address: AddressForm,
    val nickname: String,
    val tag: String?,
)
