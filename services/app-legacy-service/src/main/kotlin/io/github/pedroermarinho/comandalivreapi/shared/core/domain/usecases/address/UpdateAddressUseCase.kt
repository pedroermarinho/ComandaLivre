package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.AddressRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class UpdateAddressUseCase(
    private val addressRepository: AddressRepository,
) {
    fun update(
        id: Int,
        form: AddressForm,
    ): Result<Unit> =
        runCatching {
            val address = addressRepository.getById(id).getOrThrow()
            val updatedAddress =
                address.update(
                    street = form.street,
                    number = form.number,
                    zipCode = form.zipCode,
                    city = form.city,
                    state = form.state,
                    neighborhood = form.neighborhood,
                    complement = form.complement,
                )
            addressRepository.save(updatedAddress).map { Unit }.getOrThrow()
        }
}
