package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.feature.FeatureResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.FeaturesCatalogRecord

@Component
class FeaturePersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: FeatureEntity): Result<FeaturesCatalogRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            FeaturesCatalogRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                featureKey = entity.featureKey,
                name = entity.name,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: FeaturesCatalogRecord): Result<FeatureEntity> =
        errorDataConversion {
            FeatureEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                featureKey = record.featureKey,
                name = record.name,
                description = record.description,
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
class FeatureMapper {
    fun toDTO(entity: FeatureEntity): FeatureDTO =
        FeatureDTO(
            id = entity.id,
            featureKey = entity.featureKey,
            name = entity.name,
            description = entity.description,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: FeatureDTO): FeatureResponse =
        FeatureResponse(
            id = dto.id.publicId,
            featureKey = dto.featureKey,
            name = dto.name,
            description = dto.description,
            createdAt = dto.createdAt,
        )
}
