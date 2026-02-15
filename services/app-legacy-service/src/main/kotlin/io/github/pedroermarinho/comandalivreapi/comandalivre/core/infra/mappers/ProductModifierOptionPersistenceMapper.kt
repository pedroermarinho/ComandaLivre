package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import comandalivre.tables.records.ProductModifiersOptionsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierOptionEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class ProductModifierOptionPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ProductModifierOptionEntity): Result<ProductModifiersOptionsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ProductModifiersOptionsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                modifierGroupId = entity.modifierGroupId,
                name = entity.name,
                priceChange = entity.priceChange,
                isDefault = entity.isDefault,
                displayOrder = entity.displayOrder,
                imageAssetId = entity.imageAssetId?.value,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: ProductModifiersOptionsRecord): Result<ProductModifierOptionEntity> =
        errorDataConversion {
            ProductModifierOptionEntity(
                id = EntityId(internalId = record.id!!, publicId = record.publicId),
                modifierGroupId = record.modifierGroupId,
                name = record.name,
                priceChange = record.priceChange,
                isDefault = record.isDefault!!,
                displayOrder = record.displayOrder!!,
                imageAssetId = record.imageAssetId?.let { AssetId.restore(it) },
                audit =
                    EntityAudit(
                        createdAt = record.createdAt!!,
                        updatedAt = record.updatedAt!!,
                        deletedAt = record.deletedAt,
                        createdBy = record.createdBy,
                        updatedBy = record.updatedBy,
                        version = record.version!!,
                    ),
            )
        }
}
