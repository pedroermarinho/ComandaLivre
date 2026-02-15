package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyReportRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.EmployeeProjectAssignmentRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity.DailyActivityRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity.MyActivityRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class LogMyDailyActivityUseCase(
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val dailyReportRepository: DailyReportRepository,
    private val dailyActivityStatusRepository: DailyActivityStatusRepository,
    private val employeeProjectAssignmentRepository: EmployeeProjectAssignmentRepository,
    private val addActivityToDailyReportUseCase: AddActivityToDailyReportUseCase,
) {
    fun execute(form: MyActivityRequest): Result<EntityId> =
        runCatching {
            val report = dailyReportRepository.getById(form.projectId).getOrThrow()
            val status = dailyActivityStatusRepository.getByKey(form.statusKey).getOrThrow()
            val project = searchProjectUseCase.getById(form.projectId).getOrThrow()
            val employee = searchEmployeeUseCase.getByCompanyId(project.company.id.internalId).getOrThrow()

            val assignment =
                employeeProjectAssignmentRepository
                    .getByProjectIdAndEmployeeId(
                        projectId = project.id.internalId,
                        employeeId = employee.id.internalId,
                    ).getOrThrow()

            val activityRequest =
                DailyActivityRequest(
                    dailyReportId = report.id.publicId,
                    description = form.description,
                    statusId = status.id.publicId,
                    employeeId = assignment.id.publicId,
                    location = form.location,
                )

            addActivityToDailyReportUseCase.execute(activityRequest).getOrThrow()
        }
}
