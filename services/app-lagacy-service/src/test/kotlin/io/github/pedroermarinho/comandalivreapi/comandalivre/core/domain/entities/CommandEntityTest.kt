package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandPeople
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.MonetaryValue
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para CommandEntity")
class CommandEntityTest {
    @Test
    @DisplayName("Deve criar CommandEntity com todas as propriedades")
    fun shouldCreateCommandEntity() {
        val entityId = EntityId(internalId = 1, publicId = UUID.randomUUID())
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val commandStatus =
            CommandStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Command is open",
                audit = audit,
            )
        val command =
            CommandEntity(
                id = entityId,
                name = CommandName("Command 1"),
                employeeId = EmployeeId(1),
                numberOfPeople = CommandPeople(4),
                totalAmount = MonetaryValue(BigDecimal("100.00")),
                status = commandStatus,
                tableId = TableId(1),
                userId = UserId(1),
                companyId = CompanyId(1),
                audit = audit,
                cancellationReason = null,
                cancelledByUserId = null,
                discountAmount = null,
                discountDescription = null,
            )

        assertEquals(entityId, command.id)
        assertEquals(CommandName("Command 1"), command.name)
        assertEquals(EmployeeId(1), command.employeeId)
        assertEquals(CommandPeople(4), command.numberOfPeople)
        assertEquals(MonetaryValue(BigDecimal("100.00")), command.totalAmount)
        assertEquals(commandStatus, command.status)
        assertEquals(TableId(1), command.tableId)
        assertEquals(UserId(1), command.userId)
        assertEquals(CompanyId(1), command.companyId)
        assertEquals(audit, command.audit)
        assertNull(command.cancellationReason)
        assertNull(command.cancelledByUserId)
        assertNull(command.discountAmount)
        assertNull(command.discountDescription)
    }

    @Test
    @DisplayName("Deve criar nova CommandEntity usando o método de fábrica createNew")
    fun shouldCreateNewCommandEntityUsingFactoryMethod() {
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val commandStatus =
            CommandStatus(
                id = EntityId(2, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Command is open",
                audit = audit,
            )
        val command =
            CommandEntity.createNew(
                name = "New Command",
                employeeId = 2,
                numberOfPeople = 2,
                status = commandStatus,
                tableId = 2,
                userId = 2,
                companyId = 2,
            )

        assertNotNull(command.id.publicId)
        assertTrue(command.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(CommandName("New Command"), command.name)
        assertEquals(EmployeeId(2), command.employeeId)
        assertEquals(CommandPeople(2), command.numberOfPeople)
        assertEquals(commandStatus, command.status)
        assertEquals(TableId(2), command.tableId)
        assertEquals(UserId(2), command.userId)
        assertEquals(CompanyId(2), command.companyId)
        assertNotNull(command.audit.createdAt)
        assertNull(command.cancellationReason)
        assertNull(command.cancelledByUserId)
        assertNull(command.discountAmount)
        assertNull(command.discountDescription)
        assertTrue(command.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de CommandEntity")
    fun shouldUpdateCommandEntity() {
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val commandStatus =
            CommandStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Command is open",
                audit = audit,
            )
        val initialCommand =
            CommandEntity.createNew(
                name = "Initial Command",
                employeeId = 1,
                numberOfPeople = 1,
                status = commandStatus,
                tableId = 1,
                userId = 1,
                companyId = 1,
            )

        val updatedCommand =
            initialCommand.update(
                name = "Updated Command",
                employeeId = 3,
                numberOfPeople = 5,
                totalAmount = BigDecimal("150.00"),
                userId = 3,
                cancellationReason = "Customer changed mind",
                cancelledByUserId = 3,
                discountAmount = BigDecimal("10.00"),
                discountDescription = "Loyalty discount",
            )

        assertEquals(initialCommand.id, updatedCommand.id) // ID should remain the same
        assertEquals(CommandName("Updated Command"), updatedCommand.name)
        assertEquals(EmployeeId(3), updatedCommand.employeeId)
        assertEquals(CommandPeople(5), updatedCommand.numberOfPeople)
        assertEquals(MonetaryValue(BigDecimal("150.00")), updatedCommand.totalAmount)
        assertEquals(UserId(3), updatedCommand.userId)
        assertEquals("Customer changed mind", updatedCommand.cancellationReason)
        assertEquals(UserId(3), updatedCommand.cancelledByUserId)
        assertEquals(MonetaryValue(BigDecimal("10.00")), updatedCommand.discountAmount)
        assertEquals("Loyalty discount", updatedCommand.discountDescription)
        assertNotEquals(initialCommand.audit.updatedAt, updatedCommand.audit.updatedAt) // updatedAt should change
    }
}
