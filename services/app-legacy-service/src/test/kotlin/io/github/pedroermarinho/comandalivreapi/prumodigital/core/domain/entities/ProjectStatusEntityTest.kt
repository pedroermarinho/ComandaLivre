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
@DisplayName("Teste de unidade para ProjectStatusEntity")
class ProjectStatusEntityTest {
    @Test
    @DisplayName("Deve criar ProjectStatusEntity com todas as propriedades")
    fun shouldCreateProjectStatusEntity() {
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
            ProjectStatusEntity(
                id = entityId,
                key = "ACTIVE",
                name = "Active",
                description = "Project is currently active",
                audit = audit,
            )

        assertEquals(entityId, status.id)
        assertEquals("ACTIVE", status.key)
        assertEquals("Active", status.name)
        assertEquals("Project is currently active", status.description)
        assertEquals(audit, status.audit)
    }
}
