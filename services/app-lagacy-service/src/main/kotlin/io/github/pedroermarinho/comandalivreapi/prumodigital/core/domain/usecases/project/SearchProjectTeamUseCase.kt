package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.EmployeeProjectAssignmentDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.EmployeeProjectAssignmentRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.EmployeeProjectAssignmentMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchProjectTeamUseCase(
    private val employeeProjectAssignmentRepository: EmployeeProjectAssignmentRepository,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
    private val employeeProjectAssignmentMapper: EmployeeProjectAssignmentMapper,
) {
    fun getAll(
        pageable: PageableDTO,
        projectId: UUID,
    ): Result<PageDTO<EmployeeProjectAssignmentDTO>> =
        runCatching {
            val project = searchProjectUseCase.getById(projectId).getOrThrow()
            employeeProjectAssignmentRepository
                .getAll(pageable, project.id.internalId)
                .map { page ->
                    page.map {
                        employeeProjectAssignmentMapper
                            .toDTO(
                                entity = it,
                            ).getOrThrow()
                    }
                }.getOrThrow()
        }

    fun getByProjectIdAndEmployeeId(
        projectId: UUID,
        employeeId: UUID,
    ): Result<EmployeeProjectAssignmentDTO> =
        runCatching {
            val project = searchProjectUseCase.getById(projectId).getOrThrow()
            val employee = searchEmployeeUseCase.getById(employeeId).getOrThrow()
            val employeeProjectAssignment =
                employeeProjectAssignmentRepository
                    .getByProjectIdAndEmployeeId(
                        projectId = project.id.internalId,
                        employeeId = employee.id.internalId,
                    ).getOrThrow()
            employeeProjectAssignmentMapper.toDTO(employeeProjectAssignment).getOrThrow()
        }

    fun existByProjectIdAndEmployeeId(
        projectId: Int,
        employeeId: Int,
    ): Boolean = employeeProjectAssignmentRepository.existByProjectIdAndEmployeeId(projectId, employeeId)
}
