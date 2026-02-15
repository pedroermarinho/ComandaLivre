package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.AddAddressToUserForm
import org.springframework.stereotype.Repository

@Repository
interface UserAddressRepository {
    fun addAddressToUser(
        userId: Int,
        form: AddAddressToUserForm,
    ): Result<Unit>
}
