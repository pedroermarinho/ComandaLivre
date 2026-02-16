package io.github.pedroermarinho.prumodigital.domain.usecases.dailyattendance

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyAttendanceDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyAttendanceEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyAttendanceRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.SearchDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyAttendanceMapper
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
@UseCase
class SearchAttendanceUseCase(
    private val dailyAttendanceRepository: DailyAttendanceRepository,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
    private val dailyAttendanceMapper: DailyAttendanceMapper,
) {
    private val log = KotlinLogging.logger {}

    fun getAll(
        pageable: PageableDTO,
        dailyReportId: UUID,
    ): Result<PageDTO<DailyAttendanceDTO>> =
        runCatching {
            val dailyReport = searchDailyReportUseCase.getById(dailyReportId).getOrThrow()
            dailyAttendanceRepository
                .getAll(pageable = pageable, dailyReportId = dailyReport.id.internalId)
                .map { page -> page.map { convert(it).getOrThrow() } }
                .getOrThrow()
        }

    fun getById(id: Int): Result<DailyAttendanceDTO> {
        return runCatching {
            return dailyAttendanceRepository
                .getById(id)
                .map { convert(it).getOrThrow() }
        }
    }

    fun getById(publicId: UUID): Result<DailyAttendanceDTO> {
        return runCatching {
            return dailyAttendanceRepository
                .getById(publicId)
                .map { convert(it).getOrThrow() }
        }
    }

    private fun convert(entity: DailyAttendanceEntity): Result<DailyAttendanceDTO> =
        runCatching {
            dailyAttendanceMapper.toDTO(
                entity = entity,
                dailyReport = searchDailyReportUseCase.getById(entity.dailyReportId).getOrThrow(),
                employeeProjectAssignment =
                    searchEmployeeProjectAssignmentUseCase
                        .getById(entity.employeeAssignmentId)
                        .getOrThrow(),
            )
        }.onFailure { log.error(it) { "Erro ao converter DailyAttendanceEntity para DailyAttendanceDTO para o ID da entidade: ${entity.id}" } }
}
