package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.CommandStatusRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandStatusResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class CommandStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: CommandStatus): Result<CommandStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            CommandStatusRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key.value,
                name = entity.name.value,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: CommandStatusRecord): Result<CommandStatus> =
        errorDataConversion {
            CommandStatus(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = TypeKey.restore(record.key),
                name = TypeName.restore(record.name),
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
class CommandStatusMapper {
    fun toDTO(entity: CommandStatus) =
        CommandStatusDTO(
            id = entity.id,
            key = entity.key.value,
            name = entity.name.value,
            description = entity.description,
        )

    fun toResponse(dto: CommandStatusDTO) =
        CommandStatusResponse(
            id = dto.id.publicId,
            name = dto.name,
            key = dto.key,
            description = dto.description,
        )
}
