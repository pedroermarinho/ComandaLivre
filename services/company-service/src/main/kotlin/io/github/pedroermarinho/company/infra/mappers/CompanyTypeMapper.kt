package io.github.pedroermarinho.company.infra.mappers

import company.tables.records.CompanyTypesRecord
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanyTypeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class CompanyTypePersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: CompanyType): Result<CompanyTypesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            CompanyTypesRecord(
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

    fun toEntity(record: CompanyTypesRecord): Result<CompanyType> =
        errorDataConversion {
            CompanyType(
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
class CompanyTypeMapper {
    fun toDTO(entity: CompanyType) =
        CompanyTypeDTO(
            id = entity.id,
            key = entity.key.value,
            name = entity.name.value,
        )

    fun toResponse(dto: CompanyTypeDTO) =
        CompanyTypeResponse(
            id = dto.id.publicId,
            name = dto.name,
            key = dto.key,
        )
}
