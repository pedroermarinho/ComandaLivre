package io.github.pedroermarinho.company.domain.entities

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.UserId
import java.time.LocalDate
import java.util.*

data class EmployeeInviteEntity(
    val id: EntityId,
    val token: UUID,
    val expirationDate: LocalDate,
    val email: EmailAddress,
    val userId: UserId?,
    val companyId: CompanyId,
    val status: EmployeeInviteStatus,
    val role: RoleType,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            token: UUID,
            expirationDate: LocalDate,
            email: String,
            userId: Int?,
            companyId: Int,
            status: EmployeeInviteStatus,
            role: RoleType,
        ): EmployeeInviteEntity =
            EmployeeInviteEntity(
                id = EntityId.createNew(publicId = publicId),
                token = token,
                expirationDate = expirationDate,
                email = EmailAddress(email),
                userId = userId?.let { UserId(it) },
                companyId = CompanyId(companyId),
                status = status,
                role = role,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun updateStatus(newStatus: EmployeeInviteStatus): EmployeeInviteEntity =
        this.copy(
            status = newStatus,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
