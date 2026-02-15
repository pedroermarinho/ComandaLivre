package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
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
@DisplayName("Teste de unidade para OrderStatusEntity")
class OrderStatusEntityTest {
    @Test
    @DisplayName("Deve criar OrderStatusEntity com todas as propriedades")
    fun shouldCreateOrderStatusEntity() {
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
        val orderStatus =
            OrderStatus(
                id = entityId,
                key = TypeKey("PENDING"),
                name = TypeName("Pending"),
                description = "Order is pending",
                audit = audit,
            )

        assertEquals(entityId, orderStatus.id)
        assertEquals(TypeKey("PENDING"), orderStatus.key)
        assertEquals(TypeName("Pending"), orderStatus.name)
        assertEquals("Order is pending", orderStatus.description)
        assertEquals(audit, orderStatus.audit)
    }
}
