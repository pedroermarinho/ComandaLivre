package io.github.pedroermarinho.prumodigital.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectStatusEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.ProjectStatusResponse
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.ProjectStatusRecord

@Component
class ProjectStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ProjectStatusEntity): Result<ProjectStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ProjectStatusRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key,
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

    fun toEntity(record: ProjectStatusRecord): Result<ProjectStatusEntity> =
        errorDataConversion {
            ProjectStatusEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = record.key,
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
class ProjectStatusMapper {
    fun toDTO(entity: ProjectStatusEntity): ProjectStatusDTO =
        ProjectStatusDTO(
            id = entity.id,
            key = entity.key,
            name = entity.name,
            description = entity.description,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: ProjectStatusDTO) =
        ProjectStatusResponse(
            id = dto.id.publicId,
            key = dto.key,
            name = dto.name,
            description = dto.description,
            createdAt = dto.createdAt,
        )
}
