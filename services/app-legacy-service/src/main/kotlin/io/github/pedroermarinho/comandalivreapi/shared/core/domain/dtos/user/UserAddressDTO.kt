package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.address.AddressDTO

data class UserAddressDTO(
    val address: AddressDTO,
    val nickname: String?,
    val tag: String?,
    val isDefault: Boolean,
)
