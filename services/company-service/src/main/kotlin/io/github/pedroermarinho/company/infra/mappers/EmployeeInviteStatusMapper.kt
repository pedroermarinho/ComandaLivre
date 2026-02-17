package io.github.pedroermarinho.company.infra.mappers

import company.tables.records.EmployeeInviteStatusRecord
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteStatusDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class EmployeeInviteStatusPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: EmployeeInviteStatus): Result<EmployeeInviteStatusRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            EmployeeInviteStatusRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key.value,
                name = entity.name.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: EmployeeInviteStatusRecord): Result<EmployeeInviteStatus> =
        errorDataConversion {
            EmployeeInviteStatus(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = TypeKey.restore(record.key),
                name = TypeName.restore(record.name),
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
class EmployeeInviteStatusMapper {
    fun toDTO(entity: EmployeeInviteStatus) =
        EmployeeInviteStatusDTO(
            id = entity.id,
            key = entity.key.value,
            name = entity.name.value,
        )
}
