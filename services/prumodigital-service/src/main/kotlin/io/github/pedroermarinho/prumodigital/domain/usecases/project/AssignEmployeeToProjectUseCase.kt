package io.github.pedroermarinho.prumodigital.domain.usecases.project

import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchRoleTypeUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.project.EmployeeProjectAssignmentForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.EmployeeProjectAssignmentRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.project.EmployeeProjectAssignmentRequest
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Transactional
@UseCase
class AssignEmployeeToProjectUseCase(
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchProjectTeamUseCase: SearchProjectTeamUseCase,
    private val assignmentRepository: EmployeeProjectAssignmentRepository,
    private val searchRoleTypeUseCase: SearchRoleTypeUseCase,
) {
    fun execute(
        projectId: UUID,
        form: EmployeeProjectAssignmentRequest,
    ): Result<Unit> =
        runCatching {
            val project = searchProjectUseCase.getById(projectId).getOrThrow()

            val employee = searchEmployeeUseCase.getById(form.employeeId).getOrThrow()

            if (searchProjectTeamUseCase.existByProjectIdAndEmployeeId(
                    projectId = project.id.internalId,
                    employeeId = employee.id.internalId,
                )
            ) {
                throw BusinessLogicException("O funcionário já está atribuído a este projeto.")
            }

            val roleType = searchRoleTypeUseCase.getByEnum(RoleTypeEnum.RESTAURANT_MANAGER).getOrThrow()

            assignmentRepository
                .create(
                    EmployeeProjectAssignmentForm(
                        employeeId = employee.id.internalId,
                        projectId = project.id.internalId,
                        assignmentStartDate = form.startDate ?: LocalDate.now(),
                        isActiveAssignment = true,
                        isProjectAdmin = form.isAdmin ?: false,
                        roleInProjectId = roleType.id.internalId,
                        isAdmin = form.isAdmin ?: false,
                        startDate = form.startDate ?: LocalDate.now(),
                    ),
                ).getOrThrow()
        }
}
