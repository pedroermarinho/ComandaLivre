package io.github.pedroermarinho.comandalivre.infra.mappers

import comandalivre.tables.records.ProductCategoriesRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductCategoryDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductCategoryResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategoryName
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class ProductCategoryPersistenceMapper(
    private val currentUserService: CurrentUserService,
) {
    fun toRecord(entity: ProductCategory): Result<ProductCategoriesRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ProductCategoriesRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                key = entity.key,
                name = entity.name.value,
                description = entity.description,
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(record: ProductCategoriesRecord): Result<ProductCategory> =
        errorDataConversion {
            ProductCategory(
                id =
                    EntityId(
                        internalId = record.id!!,
                        publicId = record.publicId,
                    ),
                key = record.key,
                name = ProductCategoryName.restore(record.name),
                description = record.description,
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

@Component
class ProductCategoryMapper {
    fun toDTO(entity: ProductCategory) =
        ProductCategoryDTO(
            id = entity.id,
            key = entity.key,
            name = entity.name.value,
            description = entity.description,
        )

    fun toResponse(dto: ProductCategoryDTO) =
        ProductCategoryResponse(
            id = dto.id.publicId,
            name = dto.name,
            key = dto.key,
            description = dto.description,
        )
}
