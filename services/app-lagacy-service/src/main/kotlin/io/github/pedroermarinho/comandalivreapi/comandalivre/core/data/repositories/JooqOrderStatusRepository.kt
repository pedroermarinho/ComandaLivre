package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.ORDER_STATUS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderStatusRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.OrderStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.OrderStatusPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.fetchPage
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class JooqOrderStatusRepository(
    private val dsl: DSLContext,
    private val orderStatusPersistenceMapper: OrderStatusPersistenceMapper,
) : OrderStatusRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<OrderStatus>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(ORDER_STATUS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(ORDER_STATUS.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, ORDER_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = baseQuery(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                orderStatusPersistenceMapper.toEntity(it.into(ORDER_STATUS)).getOrThrow()
            }
        }

    override fun getAll(): Result<List<OrderStatus>> {
        val results =
            dsl
                .select()
                .from(ORDER_STATUS)
                .where(ORDER_STATUS.DELETED_AT.isNull)
                .orderBy(ORDER_STATUS.ID.asc())
                .fetch()
        return Result.success(results.map { orderStatusPersistenceMapper.toEntity(it.into(ORDER_STATUS)).getOrThrow() })
    }

    override fun getById(orderStatusId: Int): Result<OrderStatus> {
        val result =
            dsl
                .select()
                .from(ORDER_STATUS)
                .where(ORDER_STATUS.ID.eq(orderStatusId))
                .fetchOne() ?: return Result.failure(NotFoundException("Status do pedido não encontrado"))
        return orderStatusPersistenceMapper.toEntity(result.into(ORDER_STATUS))
    }

    override fun getByName(orderStatusName: String): Result<OrderStatus> {
        val result =
            dsl
                .select()
                .from(ORDER_STATUS)
                .where(ORDER_STATUS.NAME.eq(orderStatusName))
                .fetchOne() ?: return Result.failure(NotFoundException("Status do pedido não encontrado"))
        return orderStatusPersistenceMapper.toEntity(result.into(ORDER_STATUS))
    }

    override fun getByKey(key: String): Result<OrderStatus> {
        val result =
            dsl
                .select()
                .from(ORDER_STATUS)
                .where(ORDER_STATUS.KEY.eq(key))
                .fetchOne() ?: return Result.failure(NotFoundException("Status do pedido não encontrado"))
        return orderStatusPersistenceMapper.toEntity(result.into(ORDER_STATUS))
    }

    private fun baseQuery() =
        dsl
            .select()
            .from(ORDER_STATUS)

    override fun save(entity: OrderStatus): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = orderStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(ORDER_STATUS)
                        .set(record)
                        .where(ORDER_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status do pedido")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(ORDER_STATUS)
                    .set(record)
                    .returning(ORDER_STATUS.ID, ORDER_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status do pedido")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
