package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.UUID

data class ProductCategory(
    val id: EntityId,
    val key: String,
    val name: ProductCategoryName,
    val description: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            key: String,
            name: String,
            description: String?,
            createdBy: String? = null,
        ): ProductCategory =
            ProductCategory(
                id = EntityId.Companion.createNew(publicId = publicId),
                key = key,
                name = ProductCategoryName(name),
                description = description,
                audit = EntityAudit.Companion.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        name: String,
        description: String?,
    ): ProductCategory =
        this.copy(
            name = ProductCategoryName(name),
            description = description,
            audit = this.audit.update(),
        )
}
