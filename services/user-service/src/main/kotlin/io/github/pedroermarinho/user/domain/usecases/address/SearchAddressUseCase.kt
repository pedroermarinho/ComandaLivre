package io.github.pedroermarinho.user.domain.usecases.address

import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.user.domain.repositories.AddressRepository
import io.github.pedroermarinho.user.infra.mappers.AddressMapper
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@UseCase
class SearchAddressUseCase(
    private val addressRepository: AddressRepository,
    private val addressMapper: AddressMapper,
) {
    fun getById(id: Int): Result<AddressDTO> = addressRepository.getById(id).map { addressMapper.toDTO(it) }
}
