package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.ProductModifiersGroupsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierGroupEntity
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class ProductModifierGroupPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ProductModifierGroupEntity): Result<ProductModifiersGroupsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ProductModifiersGroupsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                productId = entity.productId,
                name = entity.name,
                minSelection = entity.minSelection,
                maxSelection = entity.maxSelection,
                displayOrder = entity.displayOrder,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: ProductModifiersGroupsRecord): Result<ProductModifierGroupEntity> =
        errorDataConversion {
            ProductModifierGroupEntity(
                id = EntityId(internalId = record.id!!, publicId = record.publicId!!),
                productId = record.productId!!,
                name = record.name!!,
                minSelection = record.minSelection!!,
                maxSelection = record.maxSelection!!,
                displayOrder = record.displayOrder!!,
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
