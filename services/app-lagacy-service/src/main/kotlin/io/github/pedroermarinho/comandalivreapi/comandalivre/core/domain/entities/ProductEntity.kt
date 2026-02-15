package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.*
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.AssetId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.math.BigDecimal
import java.util.*

data class ProductEntity(
    val id: EntityId,
    val name: ProductName,
    val price: ProductPrice,
    val category: ProductCategory,
    val description: String?,
    val availability: Boolean,
    val imageAssetId: AssetId? = null,
    val servesPersons: ProductServesPersons? = null,
    val companyId: ProductCompanyId,
    val ingredients: List<String>? = null,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            name: String,
            price: BigDecimal,
            category: ProductCategory,
            description: String?,
            availability: Boolean = true,
            imageAssetId: Int? = null,
            servesPersons: Int = 1,
            companyId: Int,
            ingredients: List<String>?,
        ): ProductEntity =
            ProductEntity(
                id = EntityId.createNew(publicId = publicId),
                name = ProductName(name),
                price = ProductPrice(price),
                category = category,
                description = description,
                availability = availability,
                imageAssetId = imageAssetId?.let { AssetId(it) },
                servesPersons = ProductServesPersons(servesPersons),
                companyId = ProductCompanyId(companyId),
                ingredients = ingredients,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        price: BigDecimal,
        category: ProductCategory,
        description: String?,
        availability: Boolean,
        imageAssetId: Int?,
        servesPersons: Int?,
        ingredients: List<String>?,
    ): ProductEntity =
        this.copy(
            name = ProductName(name),
            price = ProductPrice(price),
            category = category,
            description = description,
            availability = availability,
            imageAssetId = imageAssetId?.let { AssetId(it) },
            servesPersons = servesPersons?.let { ProductServesPersons(it) },
            ingredients = ingredients,
            audit = this.audit.update(),
        )

    fun updateStatus(status: Boolean) =
        this.copy(
            availability = status,
            audit = this.audit.update(),
        )

    fun updateImage(imageId: Int) =
        this.copy(
            imageAssetId = AssetId(imageId),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
