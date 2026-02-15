package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para ProductModifierGroupEntity")
class ProductModifierGroupEntityTest {
    @Test
    @DisplayName("Deve criar ProductModifierGroupEntity com todas as propriedades")
    fun shouldCreateProductModifierGroupEntity() {
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
        val modifierGroup =
            ProductModifierGroupEntity(
                id = entityId,
                productId = 1,
                name = "Cheese Options",
                minSelection = 0,
                maxSelection = 1,
                displayOrder = 1,
                audit = audit,
            )

        assertEquals(entityId, modifierGroup.id)
        assertEquals(1, modifierGroup.productId)
        assertEquals("Cheese Options", modifierGroup.name)
        assertEquals(0, modifierGroup.minSelection)
        assertEquals(1, modifierGroup.maxSelection)
        assertEquals(1, modifierGroup.displayOrder)
        assertEquals(audit, modifierGroup.audit)
    }
}
