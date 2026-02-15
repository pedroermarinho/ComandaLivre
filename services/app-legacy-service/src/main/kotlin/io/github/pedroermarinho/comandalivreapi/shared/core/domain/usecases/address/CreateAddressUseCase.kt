package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.AddressEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.AddressRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateAddressUseCase(
    private val addressRepository: AddressRepository,
) {
    fun create(form: AddressForm): Result<EntityId> =
        runCatching {
            val address =
                AddressEntity.createNew(
                    street = form.street,
                    number = form.number,
                    zipCode = form.zipCode,
                    city = form.city,
                    state = form.state,
                    neighborhood = form.neighborhood,
                    complement = form.complement,
                )
            addressRepository.save(address).getOrThrow()
        }
}
