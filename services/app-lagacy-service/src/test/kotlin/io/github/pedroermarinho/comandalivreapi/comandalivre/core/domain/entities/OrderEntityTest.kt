package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderNotes
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderPriority
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.MonetaryValue
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para OrderEntity")
class OrderEntityTest {
    @Test
    @DisplayName("Deve criar OrderEntity com todas as propriedades")
    fun shouldCreateOrderEntity() {
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
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Command is open",
                audit = audit,
            )
        val order =
            OrderEntity(
                id = entityId,
                commandId = CommandId(1),
                productId = ProductId(1),
                status = orderStatus,
                notes = OrderNotes("Extra cheese"),
                audit = audit,
                priorityLevel = OrderPriority(5),
                basePriceAtOrder = MonetaryValue(BigDecimal("10.00")),
                totalModifiersPriceAtOrder = MonetaryValue(BigDecimal("2.00")),
            )

        assertEquals(entityId, order.id)
        assertEquals(CommandId(1), order.commandId)
        assertEquals(ProductId(1), order.productId)
        assertEquals(orderStatus, order.status)
        assertEquals(OrderNotes("Extra cheese"), order.notes)
        assertEquals(audit, order.audit)
        assertEquals(OrderPriority(5), order.priorityLevel)
        assertEquals(MonetaryValue(BigDecimal("10.00")), order.basePriceAtOrder)
        assertEquals(MonetaryValue(BigDecimal("2.00")), order.totalModifiersPriceAtOrder)
        assertNull(order.cancellationReason)
        assertNull(order.cancelledByUserId)
    }

    @Test
    @DisplayName("Deve criar nova OrderEntity usando o método de fábrica createNew")
    fun shouldCreateNewOrderEntityUsingFactoryMethod() {
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
                id = EntityId(2, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Command is open",
                audit = audit,
            )
        val order =
            OrderEntity.createNew(
                commandId = 2,
                productId = 2,
                status = orderStatus,
                notes = "No onions",
                priorityLevel = 3,
                basePriceAtOrder = BigDecimal("12.50"),
                totalModifiersPriceAtOrder = BigDecimal("1.50"),
            )

        assertNotNull(order.id.publicId)
        assertTrue(order.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(CommandId(2), order.commandId)
        assertEquals(ProductId(2), order.productId)
        assertEquals(orderStatus, order.status)
        assertEquals(OrderNotes("No onions"), order.notes)
        assertEquals(OrderPriority(3), order.priorityLevel)
        assertEquals(MonetaryValue(BigDecimal("12.50")), order.basePriceAtOrder)
        assertEquals(MonetaryValue(BigDecimal("1.50")), order.totalModifiersPriceAtOrder)
        assertNull(order.cancellationReason)
        assertNull(order.cancelledByUserId)
        assertNotNull(order.audit.createdAt)
        assertTrue(order.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de OrderEntity")
    fun shouldUpdateOrderEntity() {
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
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("OPEN"),
                name = TypeName("Open"),
                description = "Command is open",
                audit = audit,
            )
        val initialOrder =
            OrderEntity.createNew(
                commandId = 1,
                productId = 1,
                status = orderStatus,
                notes = "Initial notes",
                priorityLevel = 1,
                basePriceAtOrder = BigDecimal("5.00"),
                totalModifiersPriceAtOrder = BigDecimal("0.50"),
            )

        val updatedOrder =
            initialOrder.update(
                notes = "Updated notes",
                priorityLevel = 10,
                basePriceAtOrder = BigDecimal("15.00"),
                totalModifiersPriceAtOrder = BigDecimal("3.00"),
            )

        assertEquals(initialOrder.id, updatedOrder.id) // ID should remain the same
        assertEquals(initialOrder.commandId, updatedOrder.commandId)
        assertEquals(initialOrder.productId, updatedOrder.productId)
        assertEquals(OrderNotes("Updated notes"), updatedOrder.notes)
        assertEquals(OrderPriority(10), updatedOrder.priorityLevel)
        assertEquals(MonetaryValue(BigDecimal("15.00")), updatedOrder.basePriceAtOrder)
        assertEquals(MonetaryValue(BigDecimal("3.00")), updatedOrder.totalModifiersPriceAtOrder)
        assertNotEquals(initialOrder.audit.updatedAt, updatedOrder.audit.updatedAt) // updatedAt should change
    }
}
