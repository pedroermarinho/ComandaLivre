package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para ProductModifierOptionEntity")
class ProductModifierOptionEntityTest {
    @Test
    @DisplayName("Deve criar ProductModifierOptionEntity com todas as propriedades")
    fun shouldCreateProductModifierOptionEntity() {
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
        val modifierOption =
            ProductModifierOptionEntity(
                id = entityId,
                modifierGroupId = 1,
                name = "Extra Bacon",
                priceChange = BigDecimal("3.00"),
                isDefault = false,
                displayOrder = 1,
                imageAssetId = null,
                audit = audit,
            )

        assertEquals(entityId, modifierOption.id)
        assertEquals(1, modifierOption.modifierGroupId)
        assertEquals("Extra Bacon", modifierOption.name)
        assertEquals(BigDecimal("3.00"), modifierOption.priceChange)
        assertFalse(modifierOption.isDefault)
        assertEquals(1, modifierOption.displayOrder)
        assertNull(modifierOption.imageAssetId)
        assertEquals(audit, modifierOption.audit)
    }
}
