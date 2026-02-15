package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.AddressRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.AddressMapper
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchAddressUseCase(
    private val addressRepository: AddressRepository,
    private val addressMapper: AddressMapper,
) {
    fun getById(id: Int): Result<AddressDTO> = addressRepository.getById(id).map { addressMapper.toDTO(it) }
}
