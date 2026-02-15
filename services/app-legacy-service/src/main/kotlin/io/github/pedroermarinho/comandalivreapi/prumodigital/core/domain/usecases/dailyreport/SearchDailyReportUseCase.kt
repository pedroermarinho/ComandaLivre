package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyReportDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyReportEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyReportRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchProjectUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.weatherstatus.SearchWeatherStatusUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyReportMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchDailyReportUseCase(
    private val dailyReportRepository: DailyReportRepository,
    private val searchProjectUseCase: SearchProjectUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
    private val searchWeatherStatusUseCase: SearchWeatherStatusUseCase,
    private val dailyReportMapper: DailyReportMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): Result<DailyReportDTO> =
        runCatching {
            val dailyReport = dailyReportRepository.getById(id).getOrThrow()
            convert(dailyReport).getOrThrow()
        }

    fun getById(id: Int): Result<DailyReportDTO> =
        runCatching {
            val dailyReport = dailyReportRepository.getById(id).getOrThrow()
            convert(dailyReport).getOrThrow()
        }

    fun getByProjectId(
        projectId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<DailyReportDTO>> =
        runCatching {
            dailyReportRepository
                .getAll(projectId, pageable)
                .map { page ->
                    page.map { convert(it).getOrThrow() }
                }.getOrThrow()
        }

    fun checkDailyReportUniqueness(
        projectId: Int,
        reportDate: LocalDate,
    ): Result<Unit> =
        runCatching {
            val exists = dailyReportRepository.existsByProjectIdAndReportDate(projectId, reportDate).getOrThrow()
            if (exists) {
                return Result.failure(BusinessLogicException("Já existe um relatório diário para o projeto e data informados."))
            }
            Unit
        }

    private fun convert(entity: DailyReportEntity): Result<DailyReportDTO> =
        runCatching {
            val project = searchProjectUseCase.getById(entity.projectId).getOrThrow()
            val morningWeather = entity.morningWeatherId?.let { searchWeatherStatusUseCase.getById(it).getOrThrow() }
            val afternoonWeather = entity.afternoonWeatherId?.let { searchWeatherStatusUseCase.getById(it).getOrThrow() }
            val reportedByAssignment = entity.reportedByAssignmentId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }
            dailyReportMapper.toDTO(
                entity = entity,
                morningWeather = morningWeather,
                afternoonWeather = afternoonWeather,
                reportedByAssignment = reportedByAssignment,
                project = project,
            )
        }.onFailure { log.error(it) { "Erro ao converter CompanyEntity para CompanyDTO para o ID da entidade: ${entity.id}" } }
}
