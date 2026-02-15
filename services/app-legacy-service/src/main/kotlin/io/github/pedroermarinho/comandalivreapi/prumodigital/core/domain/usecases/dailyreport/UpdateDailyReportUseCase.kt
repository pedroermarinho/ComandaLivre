package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyReportRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyreport.DailyReportRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.weatherstatus.SearchWeatherStatusUseCase
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateDailyReportUseCase(
    private val dailyReportRepository: DailyReportRepository,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchWeatherStatusUseCase: SearchWeatherStatusUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
) {
    fun execute(
        id: UUID,
        form: DailyReportRequest,
    ): Result<Unit> =
        runCatching {
            val dailyReport = dailyReportRepository.getById(id).getOrThrow()
            val morningWeather = form.morningWeatherId?.let { searchWeatherStatusUseCase.getById(it).getOrThrow() }
            val afternoonWeather = form.afternoonWeatherId?.let { searchWeatherStatusUseCase.getById(it).getOrThrow() }
            val reportedByAssignment = form.reportedByAssignmentId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }

            val updatedDailyReport =
                dailyReport.update(
                    generalObservations = form.generalObservations,
                    morningWeatherId = morningWeather?.id?.internalId,
                    afternoonWeatherId = afternoonWeather?.id?.internalId,
                    workStartTime = form.workStartTime,
                    lunchStartTime = form.lunchStartTime,
                    lunchEndTime = form.lunchEndTime,
                    workEndTime = form.workEndTime,
                    reportedByAssignmentId = reportedByAssignment?.id?.internalId,
                )
            dailyReportRepository.save(updatedDailyReport).map { Unit }.getOrThrow()
        }
}
