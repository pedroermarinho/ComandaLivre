package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ReportText
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class DailyReportEntity(
    val id: EntityId,
    val projectId: Int,
    val reportDate: LocalDate,
    val generalObservations: ReportText?,
    val morningWeatherId: Int?,
    val afternoonWeatherId: Int?,
    val workStartTime: LocalTime?,
    val lunchStartTime: LocalTime?,
    val lunchEndTime: LocalTime?,
    val workEndTime: LocalTime?,
    val reportedByAssignmentId: Int?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            projectId: Int,
            reportDate: LocalDate,
            generalObservations: String?,
            morningWeatherId: Int?,
            afternoonWeatherId: Int?,
            workStartTime: LocalTime?,
            lunchStartTime: LocalTime?,
            lunchEndTime: LocalTime?,
            workEndTime: LocalTime?,
            reportedByAssignmentId: Int?,
        ): DailyReportEntity =
            DailyReportEntity(
                id = EntityId.createNew(publicId = publicId),
                projectId = projectId,
                reportDate = reportDate,
                generalObservations = generalObservations?.let { ReportText(it) },
                morningWeatherId = morningWeatherId,
                afternoonWeatherId = afternoonWeatherId,
                workStartTime = workStartTime,
                lunchStartTime = lunchStartTime,
                lunchEndTime = lunchEndTime,
                workEndTime = workEndTime,
                reportedByAssignmentId = reportedByAssignmentId,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        generalObservations: String?,
        morningWeatherId: Int?,
        afternoonWeatherId: Int?,
        workStartTime: LocalTime?,
        lunchStartTime: LocalTime?,
        lunchEndTime: LocalTime?,
        workEndTime: LocalTime?,
        reportedByAssignmentId: Int?,
    ): DailyReportEntity =
        this.copy(
            generalObservations = generalObservations?.let { ReportText(it) },
            morningWeatherId = morningWeatherId,
            afternoonWeatherId = afternoonWeatherId,
            workStartTime = workStartTime,
            lunchStartTime = lunchStartTime,
            lunchEndTime = lunchEndTime,
            workEndTime = workEndTime,
            reportedByAssignmentId = reportedByAssignmentId,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
