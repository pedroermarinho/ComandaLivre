package io.github.pedroermarinho.company.domain.entities

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.UserId
import java.util.*

data class EmployeeEntity(
    val id: EntityId,
    val role: RoleType,
    val companyId: CompanyId,
    val userId: UserId,
    val status: Boolean,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            role: RoleType,
            companyId: Int,
            userId: Int,
            status: Boolean,
        ): EmployeeEntity =
            EmployeeEntity(
                id = EntityId.createNew(publicId = publicId),
                role = role,
                companyId = CompanyId(companyId),
                userId = UserId(userId),
                status = status,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun updateStatus(newStatus: Boolean): EmployeeEntity =
        this.copy(
            status = newStatus,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
