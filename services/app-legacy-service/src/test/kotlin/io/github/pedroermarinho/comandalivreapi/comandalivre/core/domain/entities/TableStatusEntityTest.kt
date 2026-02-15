package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para TableStatusEntity")
class TableStatusEntityTest {
    @Test
    @DisplayName("Deve criar TableStatusEntity com todas as propriedades")
    fun shouldCreateTableStatusEntity() {
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
            TableStatus(
                id = entityId,
                key = TypeKey("AVAILABLE"),
                name = TypeName("Available"),
                description = "Table is available",
                audit = audit,
            )

        assertEquals(entityId, status.id)
        assertEquals(TypeKey("AVAILABLE"), status.key)
        assertEquals(TypeName("Available"), status.name)
        assertEquals("Table is available", status.description)
        assertEquals(audit, status.audit)
    }
}
