package io.github.pedroermarinho.user.infra.mappers

import io.github.pedroermarinho.user.domain.dtos.user.UserAddressDTO
import io.github.pedroermarinho.user.domain.entities.UserAddressEntity
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.user.domain.usecases.address.SearchAddressUseCase
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import user.tables.records.UserAddressesRecord

@Component
class UserAddressPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: UserAddressEntity): Result<UserAddressesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            UserAddressesRecord(
                userId = entity.userId,
                addressId = entity.addressId,
                nickname = entity.nickname,
                tag = entity.tag,
                isDefault = entity.isDefault,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: UserAddressesRecord): Result<UserAddressEntity> =
        errorDataConversion {
            UserAddressEntity(
                userId = record.userId,
                addressId = record.addressId,
                nickname = record.nickname,
                tag = record.tag,
                isDefault = record.isDefault!!,
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
class UserAddressMapper(
    private val searchAddressUseCase: SearchAddressUseCase,
) {
    fun toDTO(entity: UserAddressEntity) =
        runCatching {
            UserAddressDTO(
                address = searchAddressUseCase.getById(entity.addressId).getOrThrow(),
                nickname = entity.nickname,
                tag = entity.tag,
                isDefault = entity.isDefault,
            )
        }
}
