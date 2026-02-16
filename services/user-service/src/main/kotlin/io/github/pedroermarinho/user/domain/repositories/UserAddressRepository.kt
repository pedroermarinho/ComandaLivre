package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.user.domain.forms.user.AddAddressToUserForm
import org.springframework.stereotype.Repository

@Repository
interface UserAddressRepository {
    fun addAddressToUser(
        userId: Int,
        form: AddAddressToUserForm,
    ): Result<Unit>
}
