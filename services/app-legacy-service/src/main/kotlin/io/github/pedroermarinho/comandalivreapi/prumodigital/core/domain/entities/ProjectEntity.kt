package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ClientName
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ProjectCode
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ProjectName
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.MonetaryValue
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class ProjectEntity(
    val id: EntityId,
    val companyId: Int,
    val name: ProjectName,
    val code: ProjectCode,
    val addressId: Int?,
    val plannedStartDate: LocalDate?,
    val plannedEndDate: LocalDate?,
    val actualStartDate: LocalDate?,
    val actualEndDate: LocalDate?,
    val clientName: ClientName?,
    val projectStatusId: Int,
    val budget: MonetaryValue?,
    val description: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            companyId: Int,
            name: String,
            code: String,
            addressId: Int? = null,
            plannedStartDate: LocalDate? = null,
            plannedEndDate: LocalDate? = null,
            actualStartDate: LocalDate? = null,
            actualEndDate: LocalDate? = null,
            clientName: String? = null,
            projectStatusId: Int,
            budget: BigDecimal? = null,
            description: String? = null,
        ): ProjectEntity =
            ProjectEntity(
                id = EntityId.createNew(publicId = publicId),
                companyId = companyId,
                name = ProjectName(name),
                code = ProjectCode(code),
                addressId = addressId,
                plannedStartDate = plannedStartDate,
                plannedEndDate = plannedEndDate,
                actualStartDate = actualStartDate,
                actualEndDate = actualEndDate,
                clientName = clientName?.let { ClientName(it) },
                projectStatusId = projectStatusId,
                budget = budget?.let { MonetaryValue(it) },
                description = description,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        code: String,
        addressId: Int?,
        plannedStartDate: LocalDate?,
        plannedEndDate: LocalDate?,
        actualStartDate: LocalDate?,
        actualEndDate: LocalDate?,
        clientName: String?,
        projectStatusId: Int,
        budget: BigDecimal?,
        description: String?,
    ): ProjectEntity =
        this.copy(
            name = ProjectName(name),
            code = ProjectCode(code),
            addressId = addressId,
            plannedStartDate = plannedStartDate,
            plannedEndDate = plannedEndDate,
            actualStartDate = actualStartDate,
            actualEndDate = actualEndDate,
            clientName = clientName?.let { ClientName(it) },
            projectStatusId = projectStatusId,
            budget = budget?.let { MonetaryValue(it) },
            description = description,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
