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
@DisplayName("Teste de unidade para ActivityAttachmentEntity")
class ActivityAttachmentEntityTest {
    @Test
    @DisplayName("Deve criar ActivityAttachmentEntity com todas as propriedades")
    fun shouldCreateActivityAttachmentEntity() {
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
        val attachment =
            ActivityAttachmentEntity(
                id = entityId,
                dailyActivityId = 1,
                assetId = 1,
                description = "Photo of the site",
                audit = audit,
            )

        assertEquals(entityId, attachment.id)
        assertEquals(1, attachment.dailyActivityId)
        assertEquals(1, attachment.assetId)
        assertEquals("Photo of the site", attachment.description)
        assertEquals(audit, attachment.audit)
    }

    @Test
    @DisplayName("Deve criar nova ActivityAttachmentEntity usando o método de fábrica createNew")
    fun shouldCreateNewActivityAttachmentEntityUsingFactoryMethod() {
        val attachment =
            ActivityAttachmentEntity.createNew(
                dailyActivityId = 2,
                assetId = 2,
                description = "Document scan",
            )

        assertNotNull(attachment.id.publicId)
        assertTrue(attachment.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(2, attachment.dailyActivityId)
        assertEquals(2, attachment.assetId)
        assertEquals("Document scan", attachment.description)
        assertNotNull(attachment.audit.createdAt)
        assertTrue(attachment.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de ActivityAttachmentEntity")
    fun shouldUpdateActivityAttachmentEntity() {
        val initialAttachment =
            ActivityAttachmentEntity.createNew(
                dailyActivityId = 3,
                assetId = 3,
                description = "Initial description",
            )

        val updatedAttachment =
            initialAttachment.update(
                assetId = 4,
                description = "Updated description",
            )

        assertEquals(initialAttachment.id, updatedAttachment.id) // ID should remain the same
        assertEquals(initialAttachment.dailyActivityId, updatedAttachment.dailyActivityId) // dailyActivityId should remain the same
        assertEquals(4, updatedAttachment.assetId)
        assertEquals("Updated description", updatedAttachment.description)
        assertNotEquals(initialAttachment.audit.updatedAt, updatedAttachment.audit.updatedAt) // updatedAt should change
    }
}
