package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyActivityForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity.DailyActivityRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.SearchDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class AddActivityToDailyReportUseCase(
    private val dailyActivityRepository: DailyActivityRepository,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
    private val searchDailyActivityStatusUseCase: SearchDailyActivityStatusUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
) {
    fun execute(form: DailyActivityRequest): Result<EntityId> =
        runCatching {
            val dailyReport = searchDailyReportUseCase.getById(form.dailyReportId).getOrThrow()
            val status = searchDailyActivityStatusUseCase.getById(form.statusId).getOrThrow()
            val responsibleEmployeeAssignment =
                form.employeeId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }

            dailyActivityRepository
                .create(
                    DailyActivityForm(
                        dailyReportId = dailyReport.id.internalId,
                        activityDescription = form.description,
                        statusId = status.id.internalId,
                        locationDescription = form.location,
                        responsibleEmployeeAssignmentId = responsibleEmployeeAssignment?.id?.internalId,
                    ),
                ).getOrThrow()
        }
}
