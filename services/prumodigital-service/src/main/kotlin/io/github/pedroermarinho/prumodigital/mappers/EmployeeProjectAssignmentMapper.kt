package io.github.pedroermarinho.prumodigital.mappers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.RoleTypeMapper
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.EmployeeProjectAssignmentDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.EmployeeProjectAssignmentEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project.EmployeeProjectAssignmentResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.EmployeeProjectAssignmentsRecord

@Component
class EmployeeProjectAssignmentPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: EmployeeProjectAssignmentEntity): Result<EmployeeProjectAssignmentsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            EmployeeProjectAssignmentsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                employeeId = entity.employeeId,
                projectId = entity.projectId,
                roleInProjectId = entity.roleInProjectId,
                assignmentStartDate = entity.assignmentStartDate,
                assignmentEndDate = entity.assignmentEndDate,
                isActiveAssignment = entity.isActiveAssignment,
                isProjectAdmin = entity.isProjectAdmin,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: EmployeeProjectAssignmentsRecord): Result<EmployeeProjectAssignmentEntity> =
        errorDataConversion {
            EmployeeProjectAssignmentEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                employeeId = record.employeeId,
                projectId = record.projectId,
                roleInProjectId = record.roleInProjectId,
                assignmentStartDate = record.assignmentStartDate,
                assignmentEndDate = record.assignmentEndDate,
                isActiveAssignment = record.isActiveAssignment!!,
                isProjectAdmin = record.isProjectAdmin!!,
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
class EmployeeProjectAssignmentMapper(
    private val employeeMapper: EmployeeMapper,
    private val projectMapper: ProjectMapper,
    private val roleTypeMapper: RoleTypeMapper,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
) {
    fun toDTO(entity: EmployeeProjectAssignmentEntity): Result<EmployeeProjectAssignmentDTO> =
        runCatching {
            val project = searchProjectUseCase.getById(entity.projectId).getOrThrow()
            val employee = searchEmployeeUseCase.getById(entity.employeeId).getOrThrow()
            val roleType = entity.roleInProjectId?.let { roleTypeMapper.toDTO(searchRoleTypeUseCase.getById(it).getOrThrow()) }
            EmployeeProjectAssignmentDTO(
                id = entity.id,
                employee = employee,
                project = project,
                roleInProject = roleType,
                assignmentStartDate = entity.assignmentStartDate,
                assignmentEndDate = entity.assignmentEndDate,
                isActiveAssignment = entity.isActiveAssignment,
                isProjectAdmin = entity.isProjectAdmin,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
            )
        }

    fun toResponse(dto: EmployeeProjectAssignmentDTO): EmployeeProjectAssignmentResponse =
        EmployeeProjectAssignmentResponse(
            id = dto.id.publicId,
            employee = employeeMapper.toResponse(dto.employee),
            project = projectMapper.toResponse(dto.project),
            roleInProject = dto.roleInProject?.let { roleTypeMapper.toResponse(it) },
            assignmentStartDate = dto.assignmentStartDate,
            assignmentEndDate = dto.assignmentEndDate,
            isActiveAssignment = dto.isActiveAssignment,
            isProjectAdmin = dto.isProjectAdmin,
            createdAt = dto.createdAt,
        )
}
