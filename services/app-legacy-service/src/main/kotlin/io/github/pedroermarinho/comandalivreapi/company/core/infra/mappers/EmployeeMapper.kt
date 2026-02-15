package io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers

import company.tables.records.CompanyTypesRecord
import company.tables.records.EmployeesRecord
import company.tables.records.RoleTypesRecord
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeSummaryResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.UserId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.UserMapper
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class EmployeePersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val roleTypePersistenceMapper: RoleTypePersistenceMapper,
) {
    fun toRecord(entity: EmployeeEntity): Result<EmployeesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            EmployeesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                roleId = entity.role.id.internalId,
                companyId = entity.companyId.value,
                userId = entity.userId.value,
                status = entity.status,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        employeesRecord: EmployeesRecord,
        roleTypesRecord: RoleTypesRecord,
        companyTypesRecord: CompanyTypesRecord,
    ): Result<EmployeeEntity> =
        errorDataConversion {
            EmployeeEntity(
                id =
                    EntityId(
                        internalId = employeesRecord.id!!,
                        publicId = employeesRecord.publicId,
                    ),
                role = roleTypePersistenceMapper.toEntity(roleTypesRecord = roleTypesRecord, companyTypesRecord = companyTypesRecord).getOrThrow(),
                companyId = CompanyId.restore(employeesRecord.companyId),
                userId = UserId.restore(employeesRecord.userId),
                status = employeesRecord.status!!,
                audit =
                    EntityAudit(
                        createdAt = employeesRecord.createdAt!!,
                        updatedAt = employeesRecord.updatedAt!!,
                        deletedAt = employeesRecord.deletedAt,
                        createdBy = employeesRecord.createdBy,
                        updatedBy = employeesRecord.updatedBy,
                        version = employeesRecord.version!!,
                    ),
            )
        }
}

@Component
class EmployeeMapper(
    private val companyMapper: CompanyMapper,
    private val roleTypeMapper: RoleTypeMapper,
    private val userMapper: UserMapper,
) {
    fun toDTO(
        entity: EmployeeEntity,
        company: CompanyDTO,
        user: UserDTO,
    ) = EmployeeDTO(
        id = entity.id,
        role = roleTypeMapper.toDTO(entity.role),
        company = company,
        user = user,
        status = entity.status,
        createdAt = entity.audit.createdAt,
    )

    fun toResponse(dto: EmployeeDTO) =
        EmployeeResponse(
            id = dto.id.publicId,
            role = roleTypeMapper.toResponse(dto.role),
            company = companyMapper.toSummaryResponse(dto.company),
            user = userMapper.toResponse(dto.user),
            status = dto.status,
            createdAt = dto.createdAt,
        )

    fun toSummaryResponseDTO(dto: EmployeeDTO) =
        EmployeeSummaryResponse(
            id = dto.id.publicId,
            role = roleTypeMapper.toResponse(dto.role),
            status = dto.status,
            createdAt = dto.createdAt,
        )
}
