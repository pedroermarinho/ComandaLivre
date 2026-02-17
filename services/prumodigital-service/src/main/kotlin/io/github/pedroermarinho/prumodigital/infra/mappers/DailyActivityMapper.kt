package io.github.pedroermarinho.prumodigital.infra.mappers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyActivityDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyActivityStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.DailyReportDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.EmployeeProjectAssignmentDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyActivityEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.dailyactivity.DailyActivityResponse
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component
import prumodigital.tables.records.DailyActivitiesRecord

@Component
class DailyActivityPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: DailyActivityEntity): Result<DailyActivitiesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            DailyActivitiesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                dailyReportId = entity.dailyReportId,
                activityDescription = entity.activityDescription,
                statusId = entity.statusId,
                locationDescription = entity.locationDescription,
                responsibleEmployeeAssignmentId = entity.responsibleEmployeeAssignmentId,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: DailyActivitiesRecord): Result<DailyActivityEntity> =
        errorDataConversion {
            DailyActivityEntity(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                dailyReportId = record.dailyReportId,
                activityDescription = record.activityDescription,
                statusId = record.statusId,
                locationDescription = record.locationDescription,
                responsibleEmployeeAssignmentId = record.responsibleEmployeeAssignmentId,
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
class DailyActivityMapper(
    private val dailyReportMapper: DailyReportMapper,
    private val dailyActivityStatusMapper: DailyActivityStatusMapper,
    private val employeeProjectAssignmentMapper: EmployeeProjectAssignmentMapper,
) {
    fun toDTO(
        entity: DailyActivityEntity,
        dailyReport: DailyReportDTO,
        status: DailyActivityStatusDTO,
        responsibleEmployeeAssignment: EmployeeProjectAssignmentDTO?,
    ): DailyActivityDTO =
        DailyActivityDTO(
            id = entity.id,
            dailyReport = dailyReport,
            activityDescription = entity.activityDescription,
            status = status,
            locationDescription = entity.locationDescription,
            responsibleEmployeeAssignment = responsibleEmployeeAssignment,
            createdAt = entity.audit.createdAt,
        )

    fun toResponse(dto: DailyActivityDTO) =
        DailyActivityResponse(
            id = dto.id.publicId,
            dailyReport = dailyReportMapper.toResponse(dto.dailyReport),
            activityDescription = dto.activityDescription,
            status = dailyActivityStatusMapper.toResponse(dto.status),
            locationDescription = dto.locationDescription,
            responsibleEmployeeAssignment =
                dto.responsibleEmployeeAssignment?.let {
                    employeeProjectAssignmentMapper.toResponse(it)
                },
            createdAt = dto.createdAt,
        )
}
