package io.github.pedroermarinho.user.domain.usecases.user

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.forms.user.AddAddressToUserForm
import io.github.pedroermarinho.user.domain.repositories.UserAddressRepository
import io.github.pedroermarinho.user.domain.request.user.AddAddressToUserRequest
import io.github.pedroermarinho.user.domain.usecases.address.CreateAddressUseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class AddAddressToUserUseCase(
    private val userAddressRepository: UserAddressRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
) {
    fun execute(form: AddAddressToUserRequest): Result<Unit> =
        runCatching {
            val userId = currentUserUseCase.getUserId().getOrThrow()

            currentUserUseCase.checkUserEditingOwnResource(userId).getOrThrow()

            val addressId = createAddressUseCase.create(form.address).getOrThrow()

            return userAddressRepository.addAddressToUser(
                userId = userId,
                form =
                    AddAddressToUserForm(
                        addressId = addressId.internalId,
                        nickname = form.nickname,
                        tag = form.tag,
                    ),
            )
        }
}
