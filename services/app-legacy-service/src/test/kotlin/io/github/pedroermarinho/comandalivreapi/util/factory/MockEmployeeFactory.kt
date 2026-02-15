
package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.UserId
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import java.time.LocalDateTime
import java.util.UUID

object MockEmployeeFactory {
    fun buildEmployeeEntity(): EmployeeEntity =
        EmployeeEntity(
            id = EntityId(1, UUID.randomUUID()),
            role = MockUserFactory.buildRoleTypeValueObject(),
            companyId = CompanyId(MockConstants.COMPANY_ID_INT),
            userId = UserId(MockConstants.USER_ID_INT),
            status = true,
            audit = EntityAudit.createNew(),
        )

    fun buildEmployeeDTO(): EmployeeDTO =
        EmployeeDTO(
            id = EntityId(1, UUID.randomUUID()),
            role = MockUserFactory.buildRoleType(),
            user = MockUserFactory.build(),
            company = MockCompanyFactory.buildCompanyDTO(),
            status = true,
            createdAt = LocalDateTime.now(),
        )
}
