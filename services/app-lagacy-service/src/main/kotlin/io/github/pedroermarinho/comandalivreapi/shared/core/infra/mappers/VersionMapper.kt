package io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.version.VersionDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.VersionEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.version.VersionResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.errorDataConversion
import org.springframework.stereotype.Component
import shared.tables.records.ApplicationVersionsRecord

@Component
class VersionPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: VersionEntity): Result<ApplicationVersionsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrNull()
            ApplicationVersionsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                applicationVersion = entity.version,
                platform = entity.platform,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth?.sub,
                updatedBy = userAuth?.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: ApplicationVersionsRecord): Result<VersionEntity> =
        errorDataConversion {
            VersionEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                version = record.applicationVersion,
                platform = record.platform,
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
class VersionMapper {
    fun toDTO(entity: VersionEntity) =
        VersionDTO(
            id = entity.id,
            version = entity.version,
            platform = entity.platform,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: VersionDTO) =
        VersionResponse(
            id = dto.id.publicId,
            version = dto.version,
            platform = dto.platform,
            createdAt = dto.createdAt,
        )
}
