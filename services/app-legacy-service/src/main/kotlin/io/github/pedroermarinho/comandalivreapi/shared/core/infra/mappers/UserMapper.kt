package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.UserEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.user.UserResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.user.UserSummaryResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserName
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.UsersRecord

@Component
class UserPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: UserEntity): Result<UsersRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            UsersRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                sub = entity.sub,
                name = entity.name.value,
                email = entity.email.value,
                avatarAssetId = entity.avatarAssetId?.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: UsersRecord): Result<UserEntity> =
        errorDataConversion {
            UserEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                sub = record.sub,
                name = UserName.restore(record.name),
                email = EmailAddress.restore(record.email),
                avatarAssetId = record.avatarAssetId?.let { AssetId.restore(it) },
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
class UserMapper {
    fun toDTO(
        entity: UserEntity,
        featureKeys: List<String>,
    ) = UserDTO(
        id = entity.id,
        sub = entity.sub,
        name = entity.name.value,
        email = entity.email.value,
        avatarAssetId = entity.avatarAssetId?.value,
        featureKeys = featureKeys,
        createdAt = entity.audit.createdAt,
        updatedAt = entity.audit.updatedAt,
        deletedAt = entity.audit.deletedAt,
    )

    fun toResponse(dto: UserDTO) =
        UserResponse(
            id = dto.id.publicId,
            name = dto.name,
            email = dto.email,
            avatarAssetId = dto.avatarAssetId,
            featureKeys = dto.featureKeys,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            deletedAt = dto.deletedAt,
        )

    fun toSummaryResponse(dto: UserDTO) =
        UserSummaryResponse(
            id = dto.id.publicId,
            name = dto.name,
            avatarAssetId = dto.avatarAssetId,
            featureKeys = dto.featureKeys,
        )
}
