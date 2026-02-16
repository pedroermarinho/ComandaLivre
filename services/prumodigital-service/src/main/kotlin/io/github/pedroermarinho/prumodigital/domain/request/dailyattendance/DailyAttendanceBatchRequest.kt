package io.github.pedroermarinho.prumodigital.domain.request.dailyattendance

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AttendanceItemRequest(
    @field:Schema(description = "ID público da alocação do funcionário no projeto.")
    @get:NotNull
    val employeeAssignmentId: UUID,
    @field:Schema(description = "Indica se o funcionário esteve presente.")
    @get:NotNull
    val isPresent: Boolean,
)

data class DailyAttendanceBatchRequest(
    @field:Schema(description = "ID público do relatório diário ao qual as presenças pertencem.")
    @get:NotNull
    val dailyReportId: UUID,
    @field:Schema(description = "Lista de presenças a serem registradas.")
    @get:NotEmpty
    @get:Valid
    val attendances: List<AttendanceItemRequest>,
)
