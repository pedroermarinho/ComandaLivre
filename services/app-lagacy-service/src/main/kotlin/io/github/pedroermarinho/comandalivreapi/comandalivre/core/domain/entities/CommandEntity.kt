package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandPeople
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.MonetaryValue
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserId
import java.math.BigDecimal
import java.util.*

data class CommandEntity(
    val id: EntityId,
    val name: CommandName,
    val employeeId: EmployeeId,
    val numberOfPeople: CommandPeople,
    val totalAmount: MonetaryValue? = null,
    val status: CommandStatus,
    val tableId: TableId,
    val userId: UserId? = null,
    val companyId: CompanyId,
    val audit: EntityAudit,
    val cancellationReason: String? = null,
    val cancelledByUserId: UserId? = null,
    val discountAmount: MonetaryValue? = null,
    val discountDescription: String? = null,
) {
    companion object {
        private val log = KotlinLogging.logger {}

        fun createNew(
            publicId: UUID? = null,
            name: String,
            employeeId: Int,
            numberOfPeople: Int,
            status: CommandStatus,
            tableId: Int,
            userId: Int?,
            companyId: Int,
        ): CommandEntity =
            CommandEntity(
                id = EntityId.createNew(publicId = publicId),
                name = CommandName(name),
                employeeId = EmployeeId(employeeId),
                numberOfPeople = CommandPeople(numberOfPeople),
                status = status,
                tableId = TableId(tableId),
                userId = userId?.let { UserId(it) },
                companyId = CompanyId(companyId),
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        employeeId: Int,
        numberOfPeople: Int,
        totalAmount: BigDecimal?,
        userId: Int?,
        cancellationReason: String?,
        cancelledByUserId: Int?,
        discountAmount: BigDecimal?,
        discountDescription: String?,
    ): CommandEntity =
        this.copy(
            name = CommandName(name),
            employeeId = EmployeeId(employeeId),
            numberOfPeople = CommandPeople(numberOfPeople),
            totalAmount = totalAmount?.let { MonetaryValue(it) },
            userId = userId?.let { UserId(it) },
            cancellationReason = cancellationReason,
            cancelledByUserId = cancelledByUserId?.let { UserId(it) },
            discountAmount = discountAmount?.let { MonetaryValue(it) },
            discountDescription = discountDescription,
            audit = this.audit.update(),
        )

    fun updateStatus(status: CommandStatus): CommandEntity =
        this.copy(
            status = status,
            audit = this.audit.update(),
        )

    fun updateTable(newTable: TableEntity): Result<CommandEntity> =
        runCatching {
            log.info { "Verificando se a comanda ${tableId.value} já está na mesa ${newTable.id.publicId}, resuldado: ${tableId.value == newTable.id.internalId}" }
            if (tableId.value == newTable.id.internalId) {
                throw BusinessLogicException("A comanda já está na mesa de destino.")
            }

            log.info {
                "Verificando a commanda ${id.internalId} e a mesa ${newTable.id.internalId} pertencem à mesma empresa, empresa da comanda: ${companyId.value}, empresa da mesa: ${newTable.companyId}"
            }
            if (companyId != newTable.companyId) {
                throw BusinessLogicException("A comanda e a mesa de destino devem pertencer à mesma empresa.")
            }

            if (status.key.value != CommandStatusEnum.OPEN.value) {
                throw BusinessLogicException("A comanda deve estar aberta para ter sua mesa alterada.")
            }

            this.copy(
                tableId = TableId(newTable.id.internalId),
                audit = this.audit.update(),
            )
        }

    fun updateTotalAmount(orders: List<OrderDTO>): CommandEntity {
        val total = orders.mapNotNull { it.basePriceAtOrder }.sumOf { it }
        log.info { "Total calculado para o comando $id: R$$total, com ${orders.size} pedidos" }
        return this.copy(
            totalAmount = MonetaryValue(total),
            audit = this.audit.update(),
        )
    }

    fun updateCancelInfo(
        cancellationReason: String,
        cancelledByUserId: Int,
    ): CommandEntity =
        this.copy(
            cancellationReason = cancellationReason,
            cancelledByUserId = UserId(cancelledByUserId),
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
