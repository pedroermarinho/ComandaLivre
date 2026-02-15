package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableReservationStatus
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import io.github.pedroermarinho.shared.valueobject.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para TableReservationEntity")
class TableReservationEntityTest {
    @Test
    @DisplayName("Deve criar TableReservationEntity com todas as propriedades")
    fun shouldCreateTableReservationEntity() {
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
        val tableReservationStatus =
            TableReservationStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("PENDING"),
                name = TypeName("Pending"),
                description = "Reservation is pending",
                audit = audit,
            )
        val reservation =
            TableReservationEntity(
                id = entityId,
                tableId = TableId(1),
                reservedFor = "John Doe",
                reservedForUserId = UserId(1),
                reservationStart = LocalDateTime.now(),
                reservationEnd = LocalDateTime.now().plusHours(2),
                status = tableReservationStatus,
                notes = "Birthday party",
                audit = audit,
            )

        assertEquals(entityId, reservation.id)
        assertEquals(TableId(1), reservation.tableId)
        assertEquals("John Doe", reservation.reservedFor)
        assertEquals(UserId(1), reservation.reservedForUserId)
        assertNotNull(reservation.reservationStart)
        assertNotNull(reservation.reservationEnd)
        assertEquals(tableReservationStatus, reservation.status)
        assertEquals("Birthday party", reservation.notes)
        assertEquals(audit, reservation.audit)
    }

    @Test
    @DisplayName("Deve criar nova TableReservationEntity usando o método de fábrica createNew")
    fun shouldCreateNewTableReservationEntityUsingFactoryMethod() {
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val tableReservationStatus =
            TableReservationStatus(
                id = EntityId(2, UUID.randomUUID()),
                key = TypeKey("CONFIRMED"),
                name = TypeName("Confirmed"),
                description = "Reservation is confirmed",
                audit = audit,
            )
        val reservation =
            TableReservationEntity.createNew(
                tableId = 2,
                reservedFor = "Jane Smith",
                reservedForUserId = 2,
                reservationStart = LocalDateTime.now().plusHours(1),
                reservationEnd = LocalDateTime.now().plusHours(3),
                status = tableReservationStatus,
                notes = "Anniversary dinner",
            )

        assertNotNull(reservation.id.publicId)
        assertTrue(reservation.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(TableId(2), reservation.tableId)
        assertEquals("Jane Smith", reservation.reservedFor)
        assertEquals(UserId(2), reservation.reservedForUserId)
        assertNotNull(reservation.reservationStart)
        assertNotNull(reservation.reservationEnd)
        assertEquals(tableReservationStatus, reservation.status)
        assertEquals("Anniversary dinner", reservation.notes)
        assertNotNull(reservation.audit.createdAt)
        assertTrue(reservation.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de TableReservationEntity")
    fun shouldUpdateTableReservationEntity() {
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )
        val tableReservationStatus =
            TableReservationStatus(
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("PENDING"),
                name = TypeName("Pending"),
                description = "Reservation is pending",
                audit = audit,
            )
        val updatedTableReservationStatus =
            TableReservationStatus(
                id = EntityId(3, UUID.randomUUID()),
                key = TypeKey("CANCELED"),
                name = TypeName("Canceled"),
                description = "Reservation is canceled",
                audit = audit,
            )
        val initialReservation =
            TableReservationEntity.createNew(
                tableId = 3,
                reservedFor = "Initial Name",
                reservedForUserId = 3,
                reservationStart = LocalDateTime.now(),
                reservationEnd = LocalDateTime.now().plusHours(1),
                status = tableReservationStatus,
                notes = "Initial notes",
            )

        val updatedReservation =
            initialReservation.updateStatus(updatedTableReservationStatus)

        assertEquals(initialReservation.id, updatedReservation.id) // ID should remain the same
        assertEquals(TableId(3), updatedReservation.tableId) // tableId should remain the same
        assertEquals(updatedTableReservationStatus, updatedReservation.status)
        assertNotEquals(initialReservation.audit.updatedAt, updatedReservation.audit.updatedAt) // updatedAt should change
    }
}
