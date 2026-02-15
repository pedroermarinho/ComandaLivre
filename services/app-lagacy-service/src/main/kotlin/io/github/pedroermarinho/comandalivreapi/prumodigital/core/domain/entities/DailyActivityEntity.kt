package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class DailyActivityEntity(
    val id: EntityId,
    val dailyReportId: Int,
    val activityDescription: String,
    val statusId: Int,
    val locationDescription: String?,
    val responsibleEmployeeAssignmentId: Int?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            dailyReportId: Int,
            activityDescription: String,
            statusId: Int,
            locationDescription: String?,
            responsibleEmployeeAssignmentId: Int?,
        ): DailyActivityEntity =
            DailyActivityEntity(
                id = EntityId.createNew(publicId = publicId),
                dailyReportId = dailyReportId,
                activityDescription = activityDescription,
                statusId = statusId,
                locationDescription = locationDescription,
                responsibleEmployeeAssignmentId = responsibleEmployeeAssignmentId,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        activityDescription: String,
        statusId: Int,
        locationDescription: String?,
        responsibleEmployeeAssignmentId: Int?,
    ): DailyActivityEntity =
        this.copy(
            activityDescription = activityDescription,
            statusId = statusId,
            locationDescription = locationDescription,
            responsibleEmployeeAssignmentId = responsibleEmployeeAssignmentId,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
