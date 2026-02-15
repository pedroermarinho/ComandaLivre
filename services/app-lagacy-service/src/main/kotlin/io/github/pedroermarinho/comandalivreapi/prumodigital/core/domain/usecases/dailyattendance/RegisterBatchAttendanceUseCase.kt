package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyattendance

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyAttendanceForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyAttendanceRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyReportRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.EmployeeProjectAssignmentRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.request.dailyattendance.DailyAttendanceBatchRequest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class RegisterBatchAttendanceUseCase(
    private val dailyAttendanceRepository: DailyAttendanceRepository,
    private val dailyReportRepository: DailyReportRepository,
    private val employeeProjectAssignmentRepository: EmployeeProjectAssignmentRepository,
) {
    fun execute(request: DailyAttendanceBatchRequest): Result<Unit> =
        runCatching {
            val dailyReport = dailyReportRepository.getById(request.dailyReportId).getOrThrow()

            request.attendances.forEach { item ->
                val assignment = employeeProjectAssignmentRepository.getById(item.employeeAssignmentId).getOrThrow()
                val form =
                    DailyAttendanceForm(
                        dailyReportId = dailyReport.id.internalId,
                        employeeAssignmentId = assignment.id.internalId,
                        present = item.isPresent,
                        // Arrival and departure times are not handled in this batch operation for simplicity
                        arrivalTime = null,
                        departureTime = null,
                        attendanceNote = if (item.isPresent) "Presente" else "Ausente",
                    )

                dailyAttendanceRepository.create(form)
            }
        }
}
