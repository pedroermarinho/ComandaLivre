package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para DailyActivityStatusEntity")
class DailyActivityStatusEntityTest {
    @Test
    @DisplayName("Deve criar DailyActivityStatusEntity com todas as propriedades")
    fun shouldCreateDailyActivityStatusEntity() {
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
            DailyActivityStatusEntity(
                id = entityId,
                key = "IN_PROGRESS",
                name = "In Progress",
                description = "Activity is currently being worked on",
                audit = audit,
            )

        assertEquals(entityId, status.id)
        assertEquals("IN_PROGRESS", status.key)
        assertEquals("In Progress", status.name)
        assertEquals("Activity is currently being worked on", status.description)
        assertEquals(audit, status.audit)
    }
}
