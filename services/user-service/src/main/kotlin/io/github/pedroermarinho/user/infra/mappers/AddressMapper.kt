package io.github.pedroermarinho.user.infra.mappers

import io.github.pedroermarinho.user.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.user.domain.entities.AddressEntity
import io.github.pedroermarinho.user.domain.response.address.AddressResponse
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.user.domain.valueobject.*
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import user.tables.records.AddressesRecord

@Component
class AddressPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: AddressEntity): Result<AddressesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            AddressesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                street = entity.street.value,
                number = entity.number,
                zipCode = entity.zipCode.value,
                city = entity.city.value,
                state = entity.state.value,
                neighborhood = entity.neighborhood.value,
                complement = entity.complement,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: AddressesRecord): Result<AddressEntity> =
        errorDataConversion {
            AddressEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                street = Street.restore(record.street),
                number = record.number,
                zipCode = ZipCode.restore(record.zipCode),
                city = City.restore(record.city),
                state = State.restore(record.state),
                neighborhood = Neighborhood.restore(record.neighborhood),
                complement = record.complement,
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}

@Component
class AddressMapper {
    fun toDTO(entity: AddressEntity) =
        AddressDTO(
            id = entity.id,
            street = entity.street.value,
            number = entity.number,
            zipCode = entity.zipCode.value,
            city = entity.city.value,
            state = entity.state.value,
            neighborhood = entity.neighborhood.value,
            complement = entity.complement,
            createdAt = entity.audit.createdAt,
            updateAt = entity.audit.updatedAt,
        )

    fun toResponse(dto: AddressDTO) =
        AddressResponse(
            id = dto.id.publicId,
            street = dto.street,
            number = dto.number,
            zipCode = dto.zipCode,
            city = dto.city,
            state = dto.state,
            neighborhood = dto.neighborhood,
            complement = dto.complement,
            createdAt = dto.createdAt,
        )
}
