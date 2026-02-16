package io.github.pedroermarinho.user.infra.mappers

import io.github.pedroermarinho.user.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.user.domain.dtos.group.UserFeatureGroupDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserDTO
import io.github.pedroermarinho.user.domain.entities.UserFeatureGroupEntity
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.UserFeatureGroupsRecord

@Component
class UserFeatureGroupPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: UserFeatureGroupEntity): Result<UserFeatureGroupsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            UserFeatureGroupsRecord(
                userId = entity.userId,
                featureGroupId = entity.featureGroupId,
                isActive = entity.isActive,
                assignedAt = entity.assignedAt,
                expiresAt = entity.expiresAt,
                notes = entity.notes,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: UserFeatureGroupsRecord): Result<UserFeatureGroupEntity> =
        errorDataConversion {
            UserFeatureGroupEntity(
                userId = record.userId,
                featureGroupId = record.featureGroupId,
                isActive = record.isActive!!,
                assignedAt = record.assignedAt!!,
                expiresAt = record.expiresAt,
                notes = record.notes,
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
class UserFeatureGroupMapper {
    fun toDTO(
        user: UserDTO,
        featureGroup: GroupDTO,
        entity: UserFeatureGroupEntity,
    ): UserFeatureGroupDTO =
        UserFeatureGroupDTO(
            user = user,
            featureGroup = featureGroup,
            isActive = entity.isActive,
            assignedAt = entity.assignedAt,
            expiresAt = entity.expiresAt,
            notes = entity.notes,
            createdAt = entity.audit.createdAt,
        )
}
