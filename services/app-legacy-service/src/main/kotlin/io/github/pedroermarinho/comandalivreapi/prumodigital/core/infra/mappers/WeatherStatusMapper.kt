package io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.WeatherStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.WeatherStatusEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.weatherstatus.WeatherStatusResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.WeatherStatusRecord

@Component
class WeatherStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: WeatherStatusEntity): Result<WeatherStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            WeatherStatusRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key,
                name = entity.name,
                description = entity.description,
                icon = entity.icon,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: WeatherStatusRecord): Result<WeatherStatusEntity> =
        errorDataConversion {
            WeatherStatusEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = record.key,
                name = record.name,
                description = record.description,
                icon = record.icon,
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
class WeatherStatusMapper {
    fun toDTO(entity: WeatherStatusEntity): WeatherStatusDTO =
        WeatherStatusDTO(
            id = entity.id,
            key = entity.key,
            name = entity.name,
            description = entity.description,
            icon = entity.icon,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: WeatherStatusDTO) =
        WeatherStatusResponse(
            id = dto.id.publicId,
            key = dto.key,
            name = dto.name,
            description = dto.description,
            icon = dto.icon,
            createdAt = dto.createdAt,
        )
}
