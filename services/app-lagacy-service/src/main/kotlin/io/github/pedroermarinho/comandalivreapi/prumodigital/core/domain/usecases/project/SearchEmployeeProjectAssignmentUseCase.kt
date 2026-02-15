package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
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
class SearchEmployeeProjectAssignmentUseCase(
    private val employeeProjectAssignmentRepository: EmployeeProjectAssignmentRepository,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val employeeProjectAssignmentMapper: EmployeeProjectAssignmentMapper,
) {
    private val log = KotlinLogging.logger {}

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
                        employeeProjectAssignmentMapper.toDTO(it).getOrThrow()
                    }
                }.getOrThrow()
        }

    fun getAll(): Result<List<EmployeeProjectAssignmentDTO>> =
        runCatching {
            employeeProjectAssignmentRepository
                .getAll()
                .getOrThrow()
                .map { employeeProjectAssignmentMapper.toDTO(it).getOrThrow() }
        }

    fun getById(id: UUID): Result<EmployeeProjectAssignmentDTO> =
        runCatching {
            employeeProjectAssignmentRepository.getById(id).map { employeeProjectAssignmentMapper.toDTO(it).getOrThrow() }.getOrThrow()
        }

    fun getById(id: Int): Result<EmployeeProjectAssignmentDTO> =
        runCatching {
            return employeeProjectAssignmentRepository
                .getById(id)
                .map { employeeProjectAssignmentMapper.toDTO(it) }
                .getOrThrow()
        }

    fun existByProjectIdAndEmployeeId(
        projectId: Int,
        employeeId: Int,
    ): Boolean = employeeProjectAssignmentRepository.existByProjectIdAndEmployeeId(projectId, employeeId)

    fun getByProjectId(projectId: Int): Result<EmployeeProjectAssignmentDTO> =
        runCatching {
            val project = searchProjectUseCase.getById(projectId).getOrThrow()
            val employee = searchEmployeeUseCase.getByCompanyId(project.company.id.internalId).getOrThrow()
            employeeProjectAssignmentRepository
                .getByProjectIdAndEmployeeId(projectId = projectId, employeeId = employee.id.internalId)
                .map { entity ->
                    employeeProjectAssignmentMapper.toDTO(entity).getOrThrow()
                }.getOrThrow()
        }
}
