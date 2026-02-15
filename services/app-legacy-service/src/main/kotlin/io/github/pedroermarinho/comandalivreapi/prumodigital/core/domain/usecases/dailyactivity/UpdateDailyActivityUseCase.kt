package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyactivity.DailyActivityRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.SearchDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateDailyActivityUseCase(
    private val dailyActivityRepository: DailyActivityRepository,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
    private val searchDailyActivityStatusUseCase: SearchDailyActivityStatusUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
) {
    fun execute(
        id: UUID,
        form: DailyActivityRequest,
    ): Result<Unit> =
        runCatching {
            dailyActivityRepository.getById(id).getOrThrow()
            val dailyReport = searchDailyReportUseCase.getById(form.dailyReportId).getOrThrow()
            val status = searchDailyActivityStatusUseCase.getById(form.statusId).getOrThrow()
            val responsibleEmployeeAssignment =
                form.employeeId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }

            val dailyActivity = dailyActivityRepository.getById(id).getOrThrow()
            val updatedDailyActivity =
                dailyActivity.update(
                    activityDescription = form.description,
                    statusId = status.id.internalId,
                    locationDescription = form.location,
                    responsibleEmployeeAssignmentId = responsibleEmployeeAssignment?.id?.internalId,
                )
            dailyActivityRepository.save(updatedDailyActivity).map { Unit }.getOrThrow()
        }
}
