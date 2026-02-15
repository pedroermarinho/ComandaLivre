package io.github.pedroermarinho.comandalivreapi.shared.core.data.repositories

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.AddAddressToUserForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserAddressRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import shared.tables.references.USER_ADDRESSES

@Repository
class UserAddressRepositoryImpl(
    private val dslContext: DSLContext,
) : UserAddressRepository {
    override fun addAddressToUser(
        userId: Int,
        form: AddAddressToUserForm,
    ): Result<Unit> {
        val rows =
            dslContext
                .insertInto(USER_ADDRESSES)
                .set(USER_ADDRESSES.USER_ID, userId)
                .set(USER_ADDRESSES.ADDRESS_ID, form.addressId)
                .set(USER_ADDRESSES.NICKNAME, form.nickname)
                .set(USER_ADDRESSES.TAG, form.tag)
                .execute()

        return if (rows > 0) {
            Result.success(Unit)
        } else {
            Result.failure(BusinessLogicException("Não foi possível adicionar o endereço ao usuário"))
        }
    }
}
