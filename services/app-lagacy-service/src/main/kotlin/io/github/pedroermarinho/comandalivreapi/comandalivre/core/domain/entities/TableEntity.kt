package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableCapacity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class TableEntity(
    val id: EntityId,
    val name: TableName,
    val numPeople: TableCapacity,
    val status: TableStatus,
    val description: String?,
    val companyId: CompanyId,
    val audit: EntityAudit,
) {
    companion object {
        private val log = KotlinLogging.logger {}

        fun createNew(
            publicId: UUID? = null,
            name: String,
            numPeople: Int,
            status: TableStatus,
            description: String?,
            companyId: Int,
        ): TableEntity =
            TableEntity(
                id = EntityId.createNew(publicId = publicId),
                name = TableName(name),
                numPeople = TableCapacity(numPeople),
                status = status,
                description = description,
                companyId = CompanyId(companyId),
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        numPeople: Int,
        description: String?,
    ): TableEntity =
        this.copy(
            name = TableName(name),
            numPeople = TableCapacity(numPeople),
            description = description,
            audit = this.audit.update(),
        )

    fun updateStatus(newStatus: TableStatus): TableEntity {
        if (status == newStatus) {
            log.info { "O status da mesa ${this.name}:${this.id.publicId} já está como ${newStatus.name}, nenhuma atualização necessária." }
            return this
        }
        return this.copy(
            status = status,
            audit = this.audit.update(),
        )
    }

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
