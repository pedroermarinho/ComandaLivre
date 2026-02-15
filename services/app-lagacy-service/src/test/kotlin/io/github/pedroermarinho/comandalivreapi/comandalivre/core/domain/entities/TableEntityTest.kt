package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableCapacity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para TableEntity")
class TableEntityTest {
    @Test
    @DisplayName("Deve criar TableEntity com todas as propriedades")
    fun shouldCreateTableEntity() {
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
        val tableStatus =
            TableStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("AVAILABLE"),
                name = TypeName("Available"),
                description = "Table is available",
                audit = audit,
            )
        val table =
            TableEntity(
                id = entityId,
                name = TableName("Table 1"),
                numPeople = TableCapacity(4),
                status = tableStatus,
                description = "Window seat",
                companyId = CompanyId(1),
                audit = audit,
            )

        assertEquals(entityId, table.id)
        assertEquals(TableName("Table 1"), table.name)
        assertEquals(TableCapacity(4), table.numPeople)
        assertEquals(tableStatus, table.status)
        assertEquals("Window seat", table.description)
        assertEquals(CompanyId(1), table.companyId)
        assertEquals(audit, table.audit)
    }

    @Test
    @DisplayName("Deve criar nova TableEntity usando o método de fábrica createNew")
    fun shouldCreateNewTableEntityUsingFactoryMethod() {
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val tableStatus =
            TableStatus(
                id = EntityId(2, UUID.randomUUID()),
                key = TypeKey("BUSY"),
                name = TypeName("Busy"),
                description = "Table is busy",
                audit = audit,
            )
        val table =
            TableEntity.createNew(
                name = "Table 2",
                numPeople = 2,
                status = tableStatus,
                description = "Near the bar",
                companyId = 1,
            )

        assertNotNull(table.id.publicId)
        assertTrue(table.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(TableName("Table 2"), table.name)
        assertEquals(TableCapacity(2), table.numPeople)
        assertEquals(tableStatus, table.status)
        assertEquals("Near the bar", table.description)
        assertEquals(CompanyId(1), table.companyId)
        assertNotNull(table.audit.createdAt)
        assertTrue(table.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de TableEntity")
    fun shouldUpdateTableEntity() {
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val tableStatus =
            TableStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("AVAILABLE"),
                name = TypeName("Available"),
                description = "Table is available",
                audit = audit,
            )
        val updatedTableStatus =
            TableStatus(
                id = EntityId(3, UUID.randomUUID()),
                key = TypeKey("CLEANING"),
                name = TypeName("Cleaning"),
                description = "Table is being cleaned",
                audit = audit,
            )
        val initialTable =
            TableEntity.createNew(
                name = "Table 3",
                numPeople = 6,
                status = tableStatus,
                description = "Outdoor seating",
                companyId = 1,
            )

        val updatedTable =
            initialTable
                .update(
                    name = "Table 3 Updated",
                    numPeople = 8,
                    description = "Outdoor seating (covered)",
                ).updateStatus(updatedTableStatus)

        assertEquals(initialTable.id, updatedTable.id) // ID should remain the same
        assertEquals(TableName("Table 3 Updated"), updatedTable.name)
        assertEquals(TableCapacity(8), updatedTable.numPeople)
        assertEquals("Outdoor seating (covered)", updatedTable.description)
        assertEquals(initialTable.companyId, updatedTable.companyId) // companyId should remain the same
        assertNotEquals(initialTable.audit.updatedAt, updatedTable.audit.updatedAt) // updatedAt should change
    }
}
