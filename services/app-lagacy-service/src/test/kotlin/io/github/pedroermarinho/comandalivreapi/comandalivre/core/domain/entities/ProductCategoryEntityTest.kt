package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategoryName
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para ProductCategoryEntity")
class ProductCategoryEntityTest {
    @Test
    @DisplayName("Deve criar ProductCategoryEntity com todas as propriedades")
    fun shouldCreateProductCategoryEntity() {
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
        val category =
            ProductCategory(
                id = entityId,
                key = "DRINKS",
                name = ProductCategoryName("Drinks"),
                description = "Beverages",
                audit = audit,
            )

        assertEquals(entityId, category.id)
        assertEquals("DRINKS", category.key)
        assertEquals(ProductCategoryName("Drinks"), category.name)
        assertEquals("Beverages", category.description)
        assertEquals(audit, category.audit)
    }

    @Test
    @DisplayName("Deve criar nova ProductCategoryEntity usando o método de fábrica createNew")
    fun shouldCreateNewProductCategoryEntityUsingFactoryMethod() {
        val category =
            ProductCategory.createNew(
                key = "FOOD",
                name = "Food",
                description = "Main courses",
            )

        assertNotNull(category.id.publicId)
        assertTrue(category.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals("FOOD", category.key)
        assertEquals(ProductCategoryName("Food"), category.name)
        assertEquals("Main courses", category.description)
        assertNotNull(category.audit.createdAt)
        assertTrue(category.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de ProductCategoryEntity")
    fun shouldUpdateProductCategoryEntity() {
        val initialCategory =
            ProductCategory.createNew(
                key = "DESSERTS",
                name = "Desserts",
                description = "Sweet treats",
            )

        val updatedCategory =
            initialCategory.update(
                name = "Sweets",
                description = "All kinds of sweets",
            )

        assertEquals(initialCategory.id, updatedCategory.id) // ID should remain the same
        assertEquals(initialCategory.key, updatedCategory.key) // Key should remain the same
        assertEquals(ProductCategoryName("Sweets"), updatedCategory.name)
        assertEquals("All kinds of sweets", updatedCategory.description)
        assertNotEquals(initialCategory.audit.updatedAt, updatedCategory.audit.updatedAt) // updatedAt should change
    }
}
