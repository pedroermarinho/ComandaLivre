package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalTime
import java.util.*

data class DailyAttendanceEntity(
    val id: EntityId,
    val dailyReportId: Int,
    val employeeAssignmentId: Int,
    val present: Boolean,
    val arrivalTime: LocalTime?,
    val departureTime: LocalTime?,
    val attendanceNote: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            dailyReportId: Int,
            employeeAssignmentId: Int,
            present: Boolean,
            arrivalTime: LocalTime?,
            departureTime: LocalTime?,
            attendanceNote: String?,
        ): DailyAttendanceEntity =
            DailyAttendanceEntity(
                id = EntityId.createNew(publicId = publicId),
                dailyReportId = dailyReportId,
                employeeAssignmentId = employeeAssignmentId,
                present = present,
                arrivalTime = arrivalTime,
                departureTime = departureTime,
                attendanceNote = attendanceNote,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        present: Boolean,
        arrivalTime: LocalTime?,
        departureTime: LocalTime?,
        attendanceNote: String?,
    ): DailyAttendanceEntity =
        this.copy(
            present = present,
            arrivalTime = arrivalTime,
            departureTime = departureTime,
            attendanceNote = attendanceNote,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
