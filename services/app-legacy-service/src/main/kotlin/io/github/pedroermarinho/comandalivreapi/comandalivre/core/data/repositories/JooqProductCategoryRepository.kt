package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.ORDER_STATUS
import comandalivre.tables.references.PRODUCT_CATEGORIES
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.ProductCategoryRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.ProductCategory
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductCategoryPersistenceMapper
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
import java.util.*

@Repository
class JooqProductCategoryRepository(
    private val dsl: DSLContext,
    private val productCategoryPersistenceMapper: ProductCategoryPersistenceMapper,
) : ProductCategoryRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<ProductCategory>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(PRODUCT_CATEGORIES.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(PRODUCT_CATEGORIES.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, ORDER_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                productCategoryPersistenceMapper.toEntity(it.into(PRODUCT_CATEGORIES)).getOrThrow()
            }
        }

    override fun getAll(): Result<List<ProductCategory>> =
        runCatching {
            query()
                .where(PRODUCT_CATEGORIES.DELETED_AT.isNull)
                .orderBy(PRODUCT_CATEGORIES.NAME.asc())
                .fetch()
                .map { productCategoryPersistenceMapper.toEntity(it.into(PRODUCT_CATEGORIES)).getOrThrow() }
        }

    override fun getById(id: Int): Result<ProductCategory> {
        val result =
            query()
                .where(PRODUCT_CATEGORIES.ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Categoria de pedido não encontrado"))
        return productCategoryPersistenceMapper.toEntity(result.into(PRODUCT_CATEGORIES))
    }

    override fun getById(id: UUID): Result<ProductCategory> {
        val result =
            query()
                .where(PRODUCT_CATEGORIES.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Categoria de pedido não encontrado"))
        return productCategoryPersistenceMapper.toEntity(result.into(PRODUCT_CATEGORIES))
    }

    override fun getByKey(key: String): Result<ProductCategory> {
        val result =
            query()
                .where(PRODUCT_CATEGORIES.KEY.eq(key))
                .fetchOne() ?: return Result.failure(NotFoundException("Categoria de pedido não encontrado"))
        return productCategoryPersistenceMapper.toEntity(result.into(PRODUCT_CATEGORIES))
    }

    override fun getIdByPublicId(id: UUID): Result<EntityId> {
        val result =
            dsl
                .select(PRODUCT_CATEGORIES.ID, PRODUCT_CATEGORIES.PUBLIC_ID)
                .from(PRODUCT_CATEGORIES)
                .where(PRODUCT_CATEGORIES.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Categoria de produto não encontrada"))

        return Result.success(EntityId(result[PRODUCT_CATEGORIES.ID]!!, result[PRODUCT_CATEGORIES.PUBLIC_ID]!!))
    }

    override fun save(entity: ProductCategory): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = productCategoryPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(PRODUCT_CATEGORIES)
                        .set(record)
                        .where(PRODUCT_CATEGORIES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a categoria de produto")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(PRODUCT_CATEGORIES)
                    .set(record)
                    .returning(PRODUCT_CATEGORIES.ID, PRODUCT_CATEGORIES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a categoria de produto")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(PRODUCT_CATEGORIES)
}
