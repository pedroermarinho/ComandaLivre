
package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeInviteStatusDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.shared.valueobject.*
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object MockEmployeeInviteFactory {
    fun buildEmployeeInviteEntity(): EmployeeInviteEntity =
        EmployeeInviteEntity(
            id = EntityId(1, UUID.randomUUID()),
            token = UUID.randomUUID(),
            expirationDate = LocalDate.now().plusDays(1),
            email = EmailAddress(MockConstants.USER_EMAIL),
            userId = UserId(MockConstants.USER_ID_INT),
            companyId = CompanyId(MockConstants.COMPANY_ID_INT),
            status = buildEmployeeInviteStatus(),
            role = MockUserFactory.buildRoleTypeValueObject(),
            audit = EntityAudit.createNew(),
        )

    fun buildEmployeeInviteDTO(): EmployeeInviteDTO =
        EmployeeInviteDTO(
            id = EntityId(1, UUID.randomUUID()),
            token = UUID.randomUUID(),
            expirationDate = LocalDate.now().plusDays(1),
            email = MockConstants.USER_EMAIL,
            user = MockUserFactory.build(),
            company = MockCompanyFactory.buildCompanyDTO(),
            role = MockUserFactory.buildRoleType(),
            status = buildEmployeeInviteStatusDTO(),
            createdAt = LocalDateTime.now(),
        )

    fun buildEmployeeInviteStatus(): EmployeeInviteStatus =
        EmployeeInviteStatus(
            id = EntityId(1, UUID.randomUUID()),
            key = TypeKey("PENDING"),
            name = TypeName("Pendente"),
            audit = EntityAudit.createNew(),
        )

    private fun buildEmployeeInviteStatusDTO(): EmployeeInviteStatusDTO =
        EmployeeInviteStatusDTO(
            id = EntityId(1, UUID.randomUUID()),
            key = "PENDING",
            name = "Pendente",
        )
}
