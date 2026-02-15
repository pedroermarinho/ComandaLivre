package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableReservationStatus
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
@DisplayName("Teste de unidade para TableReservationStatusEntity")
class TableReservationStatusEntityTest {
    @Test
    @DisplayName("Deve criar TableReservationStatusEntity com todas as propriedades")
    fun shouldCreateTableReservationStatusEntity() {
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
        val status =
            TableReservationStatus(
                id = entityId,
                key = TypeKey("CONFIRMED"),
                name = TypeName("Confirmed"),
                description = "Reservation is confirmed",
                audit = audit,
            )

        assertEquals(entityId, status.id)
        assertEquals(TypeKey("CONFIRMED"), status.key)
        assertEquals(TypeName("Confirmed"), status.name)
        assertEquals("Reservation is confirmed", status.description)
        assertEquals(audit, status.audit)
    }
}
