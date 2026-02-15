package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.group.GroupDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.GroupEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.group.GroupResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.FeatureGroupsRecord

@Component
class GroupPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: GroupEntity): Result<FeatureGroupsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            FeatureGroupsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                groupKey = entity.groupKey,
                name = entity.name,
                description = entity.description,
                createdAt = entity.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: FeatureGroupsRecord): Result<GroupEntity> =
        errorDataConversion {
            GroupEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                groupKey = record.groupKey,
                name = record.name,
                description = record.description,
                createdAt = record.createdAt!!,
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
class GroupMapper {
    fun toDTO(entity: GroupEntity): GroupDTO =
        GroupDTO(
            id = entity.id,
            groupKey = entity.groupKey,
            name = entity.name,
            description = entity.description,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: GroupDTO): GroupResponse =
        GroupResponse(
            id = dto.id.publicId,
            groupKey = dto.groupKey,
            name = dto.name,
            description = dto.description,
            createdAt = dto.createdAt,
        )
}
