package io.github.pedroermarinho.user.domain.dtos.user

import io.github.pedroermarinho.user.domain.dtos.address.AddressDTO

data class UserAddressDTO(
    val address: AddressDTO,
    val nickname: String?,
    val tag: String?,
    val isDefault: Boolean,
)
