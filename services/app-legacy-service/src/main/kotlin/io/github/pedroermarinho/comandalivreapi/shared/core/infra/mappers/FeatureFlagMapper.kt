package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.featureflag.FeatureFlagDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureFlagEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.featureflag.FeatureFlagResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.FeatureFlagsRecord

@Component
class FeatureFlagPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: FeatureFlagEntity): Result<FeatureFlagsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            FeatureFlagsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                name = entity.name,
                description = entity.description,
                keyFlag = entity.keyFlag,
                enabled = entity.enabled,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: FeatureFlagsRecord): Result<FeatureFlagEntity> =
        errorDataConversion {
            FeatureFlagEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                name = record.name,
                description = record.description,
                keyFlag = record.keyFlag,
                enabled = record.enabled!!,
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
class FeatureFlagMapper {
    fun toDTO(entity: FeatureFlagEntity) =
        FeatureFlagDTO(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            enabled = entity.enabled,
            keyFlag = entity.keyFlag,
            createdAt = entity.audit.createdAt,
            updatedAt = entity.audit.updatedAt,
        )

    fun toResponse(dto: FeatureFlagDTO) =
        FeatureFlagResponse(
            id = dto.id.publicId,
            name = dto.name,
            description = dto.description,
            enabled = dto.enabled,
            keyFlag = dto.keyFlag,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
}
