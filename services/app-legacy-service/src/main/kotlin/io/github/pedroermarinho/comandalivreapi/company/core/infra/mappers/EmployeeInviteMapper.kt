package io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers

import company.tables.records.CompanyTypesRecord
import company.tables.records.EmployeeInviteStatusRecord
import company.tables.records.EmployeeInvitesRecord
import company.tables.records.RoleTypesRecord
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeInviteResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.company.SearchCompanyUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.UserId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.UserMapper
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class EmployeeInvitePersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val employeeInviteStatusPersistenceMapper: EmployeeInviteStatusPersistenceMapper,
    private val roleTypePersistenceMapper: RoleTypePersistenceMapper,
) {
    fun toRecord(entity: EmployeeInviteEntity): Result<EmployeeInvitesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            EmployeeInvitesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                companyId = entity.companyId.value,
                roleId = entity.role.id.internalId,
                userId = entity.userId?.value,
                token = entity.token,
                email = entity.email.value,
                statusId = entity.status.id.internalId,
                expirationDate = entity.expirationDate,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        employeeInvitesRecord: EmployeeInvitesRecord,
        employeeInvitesStatusRecord: EmployeeInviteStatusRecord,
        roleTypesRecord: RoleTypesRecord,
        companyTypesRecord: CompanyTypesRecord,
    ): Result<EmployeeInviteEntity> =
        errorDataConversion {
            EmployeeInviteEntity(
                id =
                    EntityId(
                        internalId = employeeInvitesRecord.id!!,
                        publicId = employeeInvitesRecord.publicId,
                    ),
                companyId = CompanyId.restore(employeeInvitesRecord.companyId),
                role = roleTypePersistenceMapper.toEntity(roleTypesRecord = roleTypesRecord, companyTypesRecord = companyTypesRecord).getOrThrow(),
                userId = employeeInvitesRecord.userId?.let { UserId.restore(it) },
                token = employeeInvitesRecord.token,
                email = EmailAddress.restore(employeeInvitesRecord.email),
                status = employeeInviteStatusPersistenceMapper.toEntity(employeeInvitesStatusRecord).getOrThrow(),
                expirationDate = employeeInvitesRecord.expirationDate,
                audit =
                    EntityAudit(
                        createdAt = employeeInvitesRecord.createdAt!!,
                        updatedAt = employeeInvitesRecord.updatedAt!!,
                        deletedAt = employeeInvitesRecord.deletedAt,
                        createdBy = employeeInvitesRecord.createdBy,
                        updatedBy = employeeInvitesRecord.updatedBy,
                        version = employeeInvitesRecord.version!!,
                    ),
            )
        }
}

@Component
class EmployeeInviteMapper(
    private val userMapper: UserMapper,
    private val companyMapper: CompanyMapper,
    private val roleTypeMapper: RoleTypeMapper,
    private val searchUserUseCase: SearchUserUseCase,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val employeeInviteStatusMapper: EmployeeInviteStatusMapper,
) {
    fun toDTO(entity: EmployeeInviteEntity) =
        runCatching {
            val user = entity.userId?.let { searchUserUseCase.getById(it.value).getOrThrow() }
            val company = searchCompanyUseCase.getById(entity.companyId.value).getOrThrow()
            EmployeeInviteDTO(
                id = entity.id,
                email = entity.email.value,
                expirationDate = entity.expirationDate,
                user = user,
                company = company,
                role = roleTypeMapper.toDTO(entity.role),
                status = employeeInviteStatusMapper.toDTO(entity.status),
                token = entity.token,
                createdAt = entity.audit.createdAt,
            )
        }

    fun toResponse(dto: EmployeeInviteDTO) =
        EmployeeInviteResponse(
            id = dto.id.publicId,
            user = dto.user?.let { userMapper.toResponse(it) },
            company = companyMapper.toSummaryResponse(dto.company),
            role = roleTypeMapper.toResponse(dto.role),
            token = dto.token,
            createdAt = dto.createdAt,
        )
}
