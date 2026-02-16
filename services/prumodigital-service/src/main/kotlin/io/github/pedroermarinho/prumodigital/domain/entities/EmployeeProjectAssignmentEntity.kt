package io.github.pedroermarinho.prumodigital.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDate
import java.util.*

data class EmployeeProjectAssignmentEntity(
    val id: EntityId,
    val employeeId: Int,
    val projectId: Int,
    val roleInProjectId: Int?,
    val assignmentStartDate: LocalDate,
    val assignmentEndDate: LocalDate?,
    val isActiveAssignment: Boolean,
    val isProjectAdmin: Boolean,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            employeeId: Int,
            projectId: Int,
            roleInProjectId: Int?,
            assignmentStartDate: LocalDate,
            assignmentEndDate: LocalDate?,
            isActiveAssignment: Boolean,
            isProjectAdmin: Boolean,
        ): EmployeeProjectAssignmentEntity =
            EmployeeProjectAssignmentEntity(
                id = EntityId.createNew(publicId = publicId),
                employeeId = employeeId,
                projectId = projectId,
                roleInProjectId = roleInProjectId,
                assignmentStartDate = assignmentStartDate,
                assignmentEndDate = assignmentEndDate,
                isActiveAssignment = isActiveAssignment,
                isProjectAdmin = isProjectAdmin,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        roleInProjectId: Int?,
        assignmentStartDate: LocalDate,
        assignmentEndDate: LocalDate?,
        isActiveAssignment: Boolean,
        isProjectAdmin: Boolean,
    ): EmployeeProjectAssignmentEntity =
        this.copy(
            roleInProjectId = roleInProjectId,
            assignmentStartDate = assignmentStartDate,
            assignmentEndDate = assignmentEndDate,
            isActiveAssignment = isActiveAssignment,
            isProjectAdmin = isProjectAdmin,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
