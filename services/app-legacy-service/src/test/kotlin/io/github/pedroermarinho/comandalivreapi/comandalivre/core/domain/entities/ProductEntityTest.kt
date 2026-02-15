package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategoryName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCompanyId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductPrice
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductServesPersons
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para ProductEntity")
class ProductEntityTest {
    @Test
    @DisplayName("Deve criar ProductEntity com todas as propriedades")
    fun shouldCreateProductEntity() {
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
                id = EntityId(1, UUID.randomUUID()),
                key = "DRINKS",
                name = ProductCategoryName("Drinks"),
                description = "Beverages",
                audit = audit,
            )
        val product =
            ProductEntity(
                id = entityId,
                name = ProductName("Pizza"),
                price = ProductPrice(BigDecimal("25.00")),
                category = category,
                description = "Delicious pizza",
                availability = true,
                imageAssetId = AssetId(1),
                servesPersons = ProductServesPersons(2),
                companyId = ProductCompanyId(1),
                ingredients = listOf("Dough", "Tomato Sauce", "Cheese"),
                audit = audit,
            )

        assertEquals(entityId, product.id)
        assertEquals(ProductName("Pizza"), product.name)
        assertEquals(ProductPrice(BigDecimal("25.00")), product.price)
        assertEquals(category, product.category)
        assertEquals("Delicious pizza", product.description)
        assertTrue(product.availability)
        assertEquals(AssetId(1), product.imageAssetId)
        assertEquals(ProductServesPersons(2), product.servesPersons)
        assertEquals(ProductCompanyId(1), product.companyId)
        assertEquals(listOf("Dough", "Tomato Sauce", "Cheese"), product.ingredients)
        assertEquals(audit, product.audit)
    }

    @Test
    @DisplayName("Deve criar nova ProductEntity usando o método de fábrica createNew")
    fun shouldCreateNewProductEntityUsingFactoryMethod() {
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
                id = EntityId(2, UUID.randomUUID()),
                key = "FOOD",
                name = ProductCategoryName("Food"),
                description = "Main courses",
                audit = audit,
            )
        val product =
            ProductEntity.createNew(
                name = "Burger",
                price = BigDecimal("15.00"),
                category = category,
                description = "Juicy burger",
                availability = false,
                imageAssetId = null,
                servesPersons = 1,
                companyId = 1,
                ingredients = listOf("Bun", "Meat", "Lettuce"),
            )

        assertNotNull(product.id.publicId)
        assertTrue(product.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(ProductName("Burger"), product.name)
        assertEquals(ProductPrice(BigDecimal("15.00")), product.price)
        assertEquals(category, product.category)
        assertEquals("Juicy burger", product.description)
        assertFalse(product.availability)
        assertNull(product.imageAssetId)
        assertEquals(ProductServesPersons(1), product.servesPersons)
        assertEquals(ProductCompanyId(1), product.companyId)
        assertEquals(listOf("Bun", "Meat", "Lettuce"), product.ingredients)
        assertNotNull(product.audit.createdAt)
        assertTrue(product.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de ProductEntity")
    fun shouldUpdateProductEntity() {
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
                id = EntityId(3, UUID.randomUUID()),
                key = "DESSERTS",
                name = ProductCategoryName("Desserts"),
                description = "Sweet treats",
                audit = audit,
            )
        val initialProduct =
            ProductEntity.createNew(
                name = "Pasta",
                price = BigDecimal("20.00"),
                category = category,
                description = "Classic pasta",
                availability = true,
                imageAssetId = null,
                servesPersons = 1,
                companyId = 1,
                ingredients = listOf("Pasta", "Sauce"),
            )

        val updatedProduct =
            initialProduct.update(
                name = "Ravioli",
                price = BigDecimal("30.00"),
                category = category,
                description = "Fresh ravioli",
                availability = false,
                imageAssetId = 2,
                servesPersons = 3,
                ingredients = listOf("Ravioli", "Pesto"),
            )

        assertEquals(initialProduct.id, updatedProduct.id) // ID should remain the same
        assertEquals(ProductName("Ravioli"), updatedProduct.name)
        assertEquals(ProductPrice(BigDecimal("30.00")), updatedProduct.price)
        assertEquals(category, updatedProduct.category)
        assertEquals("Fresh ravioli", updatedProduct.description)
        assertFalse(updatedProduct.availability)
        assertEquals(AssetId(2), updatedProduct.imageAssetId)
        assertEquals(ProductServesPersons(3), updatedProduct.servesPersons)
        assertEquals(listOf("Ravioli", "Pesto"), updatedProduct.ingredients)
        assertNotEquals(initialProduct.audit.updatedAt, updatedProduct.audit.updatedAt) // updatedAt should change
    }
}
