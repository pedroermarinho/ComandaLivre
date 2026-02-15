package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyattendance

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyAttendanceForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyAttendanceRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyattendance.DailyAttendanceRequest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyreport.SearchDailyReportUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.project.SearchEmployeeProjectAssignmentUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class RegisterAttendanceUseCase(
    private val dailyAttendanceRepository: DailyAttendanceRepository,
    private val searchDailyReportUseCase: SearchDailyReportUseCase,
    private val searchEmployeeProjectAssignmentUseCase: SearchEmployeeProjectAssignmentUseCase,
) {
    fun execute(form: DailyAttendanceRequest): Result<Unit> =
        runCatching {
            val dailyReport = searchDailyReportUseCase.getById(form.dailyReportId).getOrThrow()
            val employeeAssignment =
                searchEmployeeProjectAssignmentUseCase
                    .getByProjectId(
                        projectId = dailyReport.project.id.internalId,
                    ).getOrThrow()
            dailyAttendanceRepository
                .create(
                    DailyAttendanceForm(
                        dailyReportId = dailyReport.id.internalId,
                        employeeAssignmentId = employeeAssignment.id.internalId,
                        present = form.present,
                        arrivalTime = form.arrivalTime,
                        departureTime = form.departureTime,
                        attendanceNote = form.attendanceNote,
                    ),
                ).getOrThrow()
        }
}
