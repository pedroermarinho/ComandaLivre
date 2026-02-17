package io.github.pedroermarinho.prumodigital.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyActivityStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyActivityStatusEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyactivity.DailyActivityStatusResponse
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.DailyActivityStatusRecord

@Component
class DailyActivityStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: DailyActivityStatusEntity): Result<DailyActivityStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            DailyActivityStatusRecord(
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

    fun toEntity(record: DailyActivityStatusRecord): Result<DailyActivityStatusEntity> =
        errorDataConversion {
            DailyActivityStatusEntity(
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
class DailyActivityStatusMapper {
    fun toDTO(entity: DailyActivityStatusEntity): DailyActivityStatusDTO =
        DailyActivityStatusDTO(
            id = entity.id,
            key = entity.key,
            name = entity.name,
            description = entity.description,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: DailyActivityStatusDTO) =
        DailyActivityStatusResponse(
            id = dto.id.publicId,
            key = dto.key,
            name = dto.name,
            description = dto.description,
            createdAt = dto.createdAt,
        )
}
