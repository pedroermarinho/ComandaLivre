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
@DisplayName("Teste de unidade para OrderItemModifierEntity")
class OrderItemModifierEntityTest {
    @Test
    @DisplayName("Deve criar OrderItemModifierEntity com todas as propriedades")
    fun shouldCreateOrderItemModifierEntity() {
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
        val modifier =
            OrderItemModifierEntity(
                id = entityId,
                orderItemId = 1,
                modifierOptionId = 1,
                priceAtOrder = BigDecimal("2.50"),
                audit = audit,
            )

        assertEquals(entityId, modifier.id)
        assertEquals(1, modifier.orderItemId)
        assertEquals(1, modifier.modifierOptionId)
        assertEquals(BigDecimal("2.50"), modifier.priceAtOrder)
        assertEquals(audit, modifier.audit)
    }
}
