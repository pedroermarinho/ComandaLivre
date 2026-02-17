package io.github.pedroermarinho.prumodigital.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyReportDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.EmployeeProjectAssignmentDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.WeatherStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyReportEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyreport.DailyReportResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ReportText
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.DailyReportsRecord

@Component
class DailyReportPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: DailyReportEntity): Result<DailyReportsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            DailyReportsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                projectId = entity.projectId,
                reportDate = entity.reportDate,
                generalObservations = entity.generalObservations?.value,
                morningWeatherId = entity.morningWeatherId,
                afternoonWeatherId = entity.afternoonWeatherId,
                workStartTime = entity.workStartTime,
                lunchStartTime = entity.lunchStartTime,
                lunchEndTime = entity.lunchEndTime,
                workEndTime = entity.workEndTime,
                reportedByAssignmentId = entity.reportedByAssignmentId,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: DailyReportsRecord): Result<DailyReportEntity> =
        errorDataConversion {
            DailyReportEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                projectId = record.projectId,
                reportDate = record.reportDate,
                generalObservations = record.generalObservations?.let { ReportText.restore(it) },
                morningWeatherId = record.morningWeatherId,
                afternoonWeatherId = record.afternoonWeatherId,
                workStartTime = record.workStartTime,
                lunchStartTime = record.lunchStartTime,
                lunchEndTime = record.lunchEndTime,
                workEndTime = record.workEndTime,
                reportedByAssignmentId = record.reportedByAssignmentId,
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
class DailyReportMapper(
    private val weatherStatusMapper: WeatherStatusMapper,
    private val employeeProjectAssignmentMapper: EmployeeProjectAssignmentMapper,
    private val projectMapper: ProjectMapper,
) {
    fun toDTO(
        morningWeather: WeatherStatusDTO? = null,
        afternoonWeather: WeatherStatusDTO? = null,
        reportedByAssignment: EmployeeProjectAssignmentDTO? = null,
        project: ProjectDTO,
        entity: DailyReportEntity,
    ): DailyReportDTO =
        DailyReportDTO(
            id = entity.id,
            reportDate = entity.reportDate,
            generalObservations = entity.generalObservations?.value,
            morningWeather = morningWeather,
            afternoonWeather = afternoonWeather,
            workStartTime = entity.workStartTime,
            lunchStartTime = entity.lunchStartTime,
            lunchEndTime = entity.lunchEndTime,
            workEndTime = entity.workEndTime,
            reportedByAssignment = reportedByAssignment,
            createdAt = entity.audit.createdAt,
            project = project,
        )

    fun toResponse(dto: DailyReportDTO): DailyReportResponse =
        DailyReportResponse(
            id = dto.id.publicId,
            reportDate = dto.reportDate,
            generalObservations = dto.generalObservations,
            morningWeather = dto.morningWeather?.let { weatherStatusMapper.toResponse(it) },
            afternoonWeather = dto.afternoonWeather?.let { weatherStatusMapper.toResponse(it) },
            project = projectMapper.toResponse(dto.project),
            workStartTime = dto.workStartTime,
            lunchStartTime = dto.lunchStartTime,
            lunchEndTime = dto.lunchEndTime,
            workEndTime = dto.workEndTime,
            reportedByAssignment =
                dto.reportedByAssignment?.let {
                    employeeProjectAssignmentMapper.toResponse(it)
                },
            createdAt = dto.createdAt,
        )
}
