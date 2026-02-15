package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.PRODUCTS
import comandalivre.tables.references.PRODUCT_CATEGORIES
import company.tables.references.COMPANIES
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductPersistenceMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import shared.tables.references.ASSETS
import java.util.*

@Repository
class JooqProductRepository(
    private val dsl: DSLContext,
    private val productPersistenceMapper: ProductPersistenceMapper,
) : ProductRepository {
    override fun getAll(
        pageable: PageableDTO,
        companyPublicId: UUID,
    ): Result<PageDTO<ProductEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(PRODUCTS.DELETED_AT.isNull)
            condition = condition.and(COMPANIES.PUBLIC_ID.eq(companyPublicId))

            if (pageable.search != null) {
                condition =
                    condition.and(
                        PRODUCTS.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(PRODUCTS.DESCRIPTION.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, PRODUCTS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                productPersistenceMapper.toEntity(productsRecord = it.into(PRODUCTS), categoryRecord = it.into(PRODUCT_CATEGORIES)).getOrThrow()
            }
        }

    override fun getById(id: UUID): Result<ProductEntity> {
        val result =
            query()
                .where(PRODUCTS.PUBLIC_ID.eq(id))
                .and(PRODUCTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Produto não encontrado"))

        return productPersistenceMapper.toEntity(productsRecord = result.into(PRODUCTS), categoryRecord = result.into(PRODUCT_CATEGORIES))
    }

    override fun getById(id: Int): Result<ProductEntity> {
        val result =
            query()
                .where(PRODUCTS.ID.eq(id))
                .and(PRODUCTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Produto não encontrado"))

        return productPersistenceMapper.toEntity(productsRecord = result.into(PRODUCTS), categoryRecord = result.into(PRODUCT_CATEGORIES))
    }

    override fun existsByPublicId(publicId: UUID): Boolean =
        dsl.fetchExists(
            dsl
                .selectOne()
                .from(PRODUCTS)
                .where(PRODUCTS.PUBLIC_ID.eq(publicId))
                .and(PRODUCTS.DELETED_AT.isNull),
        )

    override fun save(entity: ProductEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = productPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(PRODUCTS)
                        .set(record)
                        .where(PRODUCTS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o produto")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(PRODUCTS)
                    .set(record)
                    .returning(PRODUCTS.ID, PRODUCTS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o produto")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(PRODUCTS)
            .innerJoin(COMPANIES)
            .on(PRODUCTS.COMPANY_ID.eq(COMPANIES.ID))
            .innerJoin(PRODUCT_CATEGORIES)
            .on(PRODUCTS.CATEGORY_ID.eq(PRODUCT_CATEGORIES.ID))
            .leftJoin(ASSETS)
            .on(PRODUCTS.IMAGE_ASSET_ID.eq(ASSETS.ID))
}
