package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyActivityDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.SearchDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyActivityMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchDailyActivityUseCase(
    private val dailyActivityRepository: DailyActivityRepository,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
    private val searchDailyActivityStatusUseCase: SearchDailyActivityStatusUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
    private val dailyActivityMapper: DailyActivityMapper,
) {
    fun getById(id: Int): Result<DailyActivityDTO> =
        runCatching {
            val dailyActivity = dailyActivityRepository.getById(id).getOrThrow()
            val dailyReport = searchDailyReportUseCase.getById(dailyActivity.dailyReportId).getOrThrow()
            val status = searchDailyActivityStatusUseCase.getById(dailyActivity.statusId).getOrThrow()
            val responsibleEmployeeAssignment = dailyActivity.responsibleEmployeeAssignmentId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }
            dailyActivityMapper.toDTO(dailyActivity, dailyReport, status, responsibleEmployeeAssignment)
        }

    fun getById(id: UUID): Result<DailyActivityDTO> =
        runCatching {
            val dailyActivity = dailyActivityRepository.getById(id).getOrThrow()
            val dailyReport = searchDailyReportUseCase.getById(dailyActivity.dailyReportId).getOrThrow()
            val status = searchDailyActivityStatusUseCase.getById(dailyActivity.statusId).getOrThrow()
            val responsibleEmployeeAssignment = dailyActivity.responsibleEmployeeAssignmentId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }
            dailyActivityMapper.toDTO(dailyActivity, dailyReport, status, responsibleEmployeeAssignment)
        }

    fun getAll(
        dailyReportId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<DailyActivityDTO>> =
        runCatching {
            dailyActivityRepository
                .getByDailyReportId(dailyReportId, pageable)
                .map { page ->
                    page.map { dailyActivity ->
                        val dailyReport = searchDailyReportUseCase.getById(dailyActivity.dailyReportId).getOrThrow()
                        val status = searchDailyActivityStatusUseCase.getById(dailyActivity.statusId).getOrThrow()
                        val responsibleEmployeeAssignment = dailyActivity.responsibleEmployeeAssignmentId?.let { searchEmployeeProjectAssignmentUseCase.getById(it).getOrThrow() }
                        dailyActivityMapper.toDTO(dailyActivity, dailyReport, status, responsibleEmployeeAssignment)
                    }
                }.getOrThrow()
        }
}
