package io.github.pedroermarinho.company.infra.mappers

import company.tables.records.CompanyTypesRecord
import company.tables.records.RoleTypesRecord
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.RoleTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.RoleTypeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class RoleTypePersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val companyTypePersistenceMapper: CompanyTypePersistenceMapper,
) {
    fun toRecord(entity: RoleType): Result<RoleTypesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            RoleTypesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key.value,
                name = entity.name.value,
                description = entity.description,
                companyTypeId = entity.companyType.id.internalId,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        roleTypesRecord: RoleTypesRecord,
        companyTypesRecord: CompanyTypesRecord,
    ): Result<RoleType> =
        errorDataConversion {
            RoleType(
                id =
                    EntityId(
                        internalId = roleTypesRecord.id!!,
                        publicId = roleTypesRecord.publicId,
                    ),
                key = TypeKey.restore(roleTypesRecord.key),
                name = TypeName.restore(roleTypesRecord.name),
                description = roleTypesRecord.description,
                companyType = companyTypePersistenceMapper.toEntity(companyTypesRecord).getOrThrow(),
                audit =
                    EntityAudit(
                        createdAt = roleTypesRecord.createdAt!!,
                        updatedAt = roleTypesRecord.updatedAt!!,
                        deletedAt = roleTypesRecord.deletedAt,
                        createdBy = roleTypesRecord.createdBy,
                        updatedBy = roleTypesRecord.updatedBy,
                        version = roleTypesRecord.version!!,
                    ),
            )
        }
}

@Component
class RoleTypeMapper {
    fun toDTO(entity: RoleType) =
        RoleTypeDTO(
            id = entity.id,
            key = entity.key.value,
            name = entity.name.value,
        )

    fun toResponse(dto: RoleTypeDTO) =
        RoleTypeResponse(
            id = dto.id.publicId,
            name = dto.name,
            key = dto.key,
        )
}
