package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
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
@DisplayName("Teste de unidade para SessionStatusEntity")
class SessionStatusEntityTest {
    @Test
    @DisplayName("Deve criar SessionStatusEntity com todas as propriedades")
    fun shouldCreateSessionStatusEntity() {
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
        val sessionStatus =
            SessionStatus(
                id = entityId,
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Session is open",
                audit = audit,
            )

        assertEquals(entityId, sessionStatus.id)
        assertEquals(TypeKey("OPEN"), sessionStatus.key)
        assertEquals(TypeName("Open"), sessionStatus.name)
        assertEquals("Session is open", sessionStatus.description)
        assertEquals(audit, sessionStatus.audit)
    }
}
