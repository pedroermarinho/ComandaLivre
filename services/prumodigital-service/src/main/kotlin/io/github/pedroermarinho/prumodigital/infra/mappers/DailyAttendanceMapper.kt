package io.github.pedroermarinho.prumodigital.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyAttendanceDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyReportDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.EmployeeProjectAssignmentDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyAttendanceEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyattendance.DailyAttendanceResponse
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.DailyAttendancesRecord

@Component
class DailyAttendancePersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: DailyAttendanceEntity): Result<DailyAttendancesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            DailyAttendancesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                dailyReportId = entity.dailyReportId,
                employeeAssignmentId = entity.employeeAssignmentId,
                present = entity.present,
                arrivalTime = entity.arrivalTime,
                departureTime = entity.departureTime,
                attendanceNote = entity.attendanceNote,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: DailyAttendancesRecord): Result<DailyAttendanceEntity> =
        errorDataConversion {
            DailyAttendanceEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                dailyReportId = record.dailyReportId,
                employeeAssignmentId = record.employeeAssignmentId,
                present = record.present!!,
                arrivalTime = record.arrivalTime,
                departureTime = record.departureTime,
                attendanceNote = record.attendanceNote,
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}

@Component
class DailyAttendanceMapper(
    private val dailyReportMapper: DailyReportMapper,
    private val employeeProjectAssignmentMapper: EmployeeProjectAssignmentMapper,
) {
    fun toDTO(
        entity: DailyAttendanceEntity,
        dailyReport: DailyReportDTO,
        employeeProjectAssignment: EmployeeProjectAssignmentDTO,
    ): DailyAttendanceDTO =
        DailyAttendanceDTO(
            id = entity.id,
            dailyReport = dailyReport,
            employeeAssignment = employeeProjectAssignment,
            present = entity.present,
            arrivalTime = entity.arrivalTime,
            departureTime = entity.departureTime,
            attendanceNote = entity.attendanceNote,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: DailyAttendanceDTO) =
        DailyAttendanceResponse(
            id = dto.id.publicId,
            employeeAssignment = employeeProjectAssignmentMapper.toResponse(dto.employeeAssignment),
            present = dto.present,
            arrivalTime = dto.arrivalTime,
            departureTime = dto.departureTime,
            attendanceNote = dto.attendanceNote,
            createdAt = dto.createdAt,
        )
}
