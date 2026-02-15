package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import comandalivre.tables.records.ProductCategoriesRecord
import comandalivre.tables.records.ProductsRecord
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCompanyId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductName
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductPrice
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductServesPersons
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.errorDataConversion
import org.springframework.stereotype.Component

@Component
class ProductPersistenceMapper(
    private val currentUserService: CurrentUserService,
    private val productCategoryPersistenceMapper: ProductCategoryPersistenceMapper,
) {
    fun toRecord(entity: ProductEntity): Result<ProductsRecord> =
        errorDataConversion {
            val userAuth = currentUserService.getLoggedUser().getOrThrow()
            ProductsRecord(
                id = entity.id.internalId.let { if (it == 0) null else it },
                publicId = entity.id.publicId,
                name = entity.name.value,
                price = entity.price.value,
                categoryId = entity.category.id.internalId,
                description = entity.description,
                availability = entity.availability,
                companyId = entity.companyId.value,
                imageAssetId = entity.imageAssetId?.value,
                servesPersons = entity.servesPersons?.value,
                ingredients = entity.ingredients?.toTypedArray(),
                createdAt = entity.audit.createdAt,
                updatedAt = entity.audit.updatedAt,
                deletedAt = entity.audit.deletedAt,
                createdBy = entity.audit.createdBy ?: userAuth.sub,
                updatedBy = userAuth.sub,
                version = entity.audit.version,
            )
        }

    fun toEntity(
        productsRecord: ProductsRecord,
        categoryRecord: ProductCategoriesRecord,
    ): Result<ProductEntity> =
        errorDataConversion {
            ProductEntity(
                id =
                    EntityId(
                        internalId = productsRecord.id!!,
                        publicId = productsRecord.publicId,
                    ),
                name = ProductName.restore(productsRecord.name),
                price = ProductPrice.restore(productsRecord.price),
                category = productCategoryPersistenceMapper.toEntity(categoryRecord).getOrThrow(),
                description = productsRecord.description,
                availability = productsRecord.availability!!,
                companyId = ProductCompanyId.restore(productsRecord.companyId),
                imageAssetId = productsRecord.imageAssetId?.let { AssetId.restore(it) },
                servesPersons = productsRecord.servesPersons?.let { ProductServesPersons.restore(it) },
                ingredients = productsRecord.ingredients?.filterNotNull(),
                audit =
                    EntityAudit(
                        createdAt = productsRecord.createdAt!!,
                        updatedAt = productsRecord.updatedAt!!,
                        deletedAt = productsRecord.deletedAt,
                        createdBy = productsRecord.createdBy,
                        updatedBy = productsRecord.updatedBy,
                        version = productsRecord.version!!,
                    ),
            )
        }
}

@Component
class ProductMapper(
    private val productCategoryMapper: ProductCategoryMapper,
) {
    fun toDTO(
        entity: ProductEntity,
        company: CompanyDTO,
        image: String?,
    ) = ProductDTO(
        id = entity.id,
        name = entity.name.value,
        price = entity.price.value,
        image = image,
        servesPersons = entity.servesPersons?.value,
        category = productCategoryMapper.toDTO(entity.category),
        company = company,
        description = entity.description,
        availability = entity.availability,
        ingredients = entity.ingredients,
        createdAt = entity.audit.createdAt,
    )

    fun toResponse(dto: ProductDTO) =
        ProductResponse(
            id = dto.id.publicId,
            name = dto.name,
            price = dto.price,
            category = productCategoryMapper.toResponse(dto.category),
            description = dto.description,
            availability = dto.availability,
            image = dto.image,
            servesPersons = dto.servesPersons,
            ingredients = dto.ingredients,
            createdAt = dto.createdAt,
        )
}
