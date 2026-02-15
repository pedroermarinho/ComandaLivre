package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyReportEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyReportRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyreport.DailyReportRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.weatherstatus.SearchWeatherStatusUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateDailyReportUseCase(
    private val dailyReportRepository: DailyReportRepository,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchWeatherStatusUseCase: SearchWeatherStatusUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
) {
    fun execute(form: DailyReportRequest): Result<Unit> =
        runCatching {
            val project = searchProjectUseCase.getById(form.projectId).getOrThrow()
            val morningWeather = form.morningWeatherId?.let { searchWeatherStatusUseCase.getById(it).getOrThrow() }
            val afternoonWeather = form.afternoonWeatherId?.let { searchWeatherStatusUseCase.getById(it).getOrThrow() }
            val reportedByAssignment = form.reportedByAssignmentId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }

            searchDailyReportUseCase
                .checkDailyReportUniqueness(
                    projectId = project.id.internalId,
                    reportDate = form.reportDate,
                ).getOrThrow()

            val dailyReport =
                DailyReportEntity.createNew(
                    projectId = project.id.internalId,
                    reportDate = form.reportDate,
                    generalObservations = form.generalObservations,
                    morningWeatherId = morningWeather?.id?.internalId,
                    afternoonWeatherId = afternoonWeather?.id?.internalId,
                    workStartTime = form.workStartTime,
                    lunchStartTime = form.lunchStartTime,
                    lunchEndTime = form.lunchEndTime,
                    workEndTime = form.workEndTime,
                    reportedByAssignmentId = reportedByAssignment?.id?.internalId,
                )
            dailyReportRepository.save(dailyReport).getOrThrow()
        }
}
