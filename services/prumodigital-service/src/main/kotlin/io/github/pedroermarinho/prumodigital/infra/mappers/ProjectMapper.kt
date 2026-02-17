package io.github.pedroermarinho.prumodigital.infra.mappers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyMapper
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.ProjectResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ClientName
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ProjectCode
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ProjectName
import io.github.pedroermarinho.user.domain.dtos.address.AddressDTO
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.MonetaryValue
import io.github.pedroermarinho.user.infra.mappers.AddressMapper
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.ProjectsRecord

@Component
class ProjectPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ProjectEntity): Result<ProjectsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ProjectsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                companyId = entity.companyId,
                name = entity.name.value,
                code = entity.code.value,
                addressId = entity.addressId,
                plannedStartDate = entity.plannedStartDate,
                plannedEndDate = entity.plannedEndDate,
                actualStartDate = entity.actualStartDate,
                actualEndDate = entity.actualEndDate,
                clientName = entity.clientName?.value,
                projectStatusId = entity.projectStatusId,
                budget = entity.budget?.value,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: ProjectsRecord): Result<ProjectEntity> =
        errorDataConversion {
            ProjectEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                companyId = record.companyId,
                name = ProjectName.restore(record.name),
                code = ProjectCode.restore(record.code),
                addressId = record.addressId,
                plannedStartDate = record.plannedStartDate,
                plannedEndDate = record.plannedEndDate,
                actualStartDate = record.actualStartDate,
                actualEndDate = record.actualEndDate,
                clientName = record.clientName?.let { ClientName.restore(it) },
                projectStatusId = record.projectStatusId,
                budget = record.budget?.let { MonetaryValue.restore(it) },
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
class ProjectMapper(
    private val companyMapper: CompanyMapper,
    private val addressMapper: AddressMapper,
    private val projectStatusMapper: ProjectStatusMapper,
) {
    fun toDTO(
        company: CompanyDTO,
        address: AddressDTO?,
        projectStatus: ProjectStatusDTO,
        entity: ProjectEntity,
    ): ProjectDTO =
        ProjectDTO(
            id = entity.id,
            name = entity.name.value,
            code = entity.code.value,
            plannedStartDate = entity.plannedStartDate,
            plannedEndDate = entity.plannedEndDate,
            actualStartDate = entity.actualStartDate,
            actualEndDate = entity.actualEndDate,
            clientName = entity.clientName?.value,
            budget = entity.budget?.value,
            description = entity.description,
            createdAt = entity.audit.createdAt,
            company = company,
            address = address,
            projectStatus = projectStatus,
        )

    fun toResponse(dto: ProjectDTO): ProjectResponse =
        ProjectResponse(
            id = dto.id.publicId,
            company = companyMapper.toResponse(dto.company),
            name = dto.name,
            code = dto.code,
            address = dto.address?.let { addressMapper.toResponse(it) },
            plannedStartDate = dto.plannedStartDate,
            plannedEndDate = dto.plannedEndDate,
            actualStartDate = dto.actualStartDate,
            actualEndDate = dto.actualEndDate,
            clientName = dto.clientName,
            status = projectStatusMapper.toResponse(dto.projectStatus),
            budget = dto.budget,
            description = dto.description,
            createdAt = dto.createdAt,
        )
}
