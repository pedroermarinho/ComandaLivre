package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.COMMANDS
import comandalivre.tables.references.ORDER_ITEMS
import comandalivre.tables.references.ORDER_STATUS
import comandalivre.tables.references.PRODUCTS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.order.OrderFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.OrderEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.OrderRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.OrderPersistenceMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqOrderRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val orderPersistenceMapper: OrderPersistenceMapper,
) : OrderRepository {
    override fun getById(orderId: Int): Result<OrderEntity> {
        val record =
            query()
                .where(ORDER_ITEMS.ID.eq(orderId))
                .and(ORDER_ITEMS.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Pedido não encontrado"))
        return orderPersistenceMapper.toEntity(orderItemsRecord = record.into(ORDER_ITEMS), orderStatusRecord = record.into(ORDER_STATUS))
    }

    override fun getById(publicId: UUID): Result<OrderEntity> {
        val record =
            query()
                .where(ORDER_ITEMS.PUBLIC_ID.eq(publicId))
                .and(ORDER_ITEMS.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Pedido não encontrado"))
        return orderPersistenceMapper.toEntity(orderItemsRecord = record.into(ORDER_ITEMS), orderStatusRecord = record.into(ORDER_STATUS))
    }

    override fun getAll(
        pageable: PageableDTO,
        filter: OrderFilterDTO,
    ): Result<PageDTO<OrderEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(ORDER_ITEMS.DELETED_AT.isNull)

            if (filter.companyId != null) {
                condition = condition.and(COMMANDS.COMPANY_ID.eq(filter.companyId))
            }

            if (filter.commandPublicId != null) {
                condition = condition.and(COMMANDS.PUBLIC_ID.eq(filter.commandPublicId))
            }

            filter.status?.let { status ->
                if (status.isNotEmpty()) {
                    condition = condition.and(ORDER_STATUS.KEY.`in`(status))
                }
            }

            if (pageable.search != null) {
                condition =
                    condition.and(
                        PRODUCTS.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(ORDER_STATUS.NAME.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, ORDER_ITEMS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) { record ->
                orderPersistenceMapper.toEntity(orderItemsRecord = record.into(ORDER_ITEMS), orderStatusRecord = record.into(ORDER_STATUS)).getOrThrow()
            }
        }

    override fun getAll(commandId: Int): Result<List<OrderEntity>> =
        runCatching {
            val result =
                query()
                    .where(ORDER_ITEMS.COMMAND_ID.eq(commandId))
                    .and(ORDER_ITEMS.DELETED_AT.isNull)
                    .fetch()
                    .map {
                        orderPersistenceMapper.toEntity(orderItemsRecord = it.into(ORDER_ITEMS), orderStatusRecord = it.into(ORDER_STATUS)).getOrThrow()
                    }
            return Result.success(result)
        }

    private fun query() =
        dsl
            .select()
            .from(ORDER_ITEMS)
            .join(COMMANDS)
            .on(COMMANDS.ID.eq(ORDER_ITEMS.COMMAND_ID))
            .join(PRODUCTS)
            .on(PRODUCTS.ID.eq(ORDER_ITEMS.PRODUCT_ID))
            .join(ORDER_STATUS)
            .on(ORDER_STATUS.ID.eq(ORDER_ITEMS.STATUS_ID))

    override fun delete(orderId: Int): Result<Unit> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .update(ORDER_ITEMS)
                .set(ORDER_ITEMS.DELETED_AT, LocalDateTime.now())
                .set(ORDER_ITEMS.UPDATED_BY, userAuth.sub)
                .set(ORDER_ITEMS.UPDATED_AT, LocalDateTime.now())
                .where(ORDER_ITEMS.ID.eq(orderId))
                .execute()
        if (result == 0) {
            return Result.failure(NotFoundException("Pedido não encontrado"))
        }
        return Result.success(Unit)
    }

    override fun save(entity: OrderEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = orderPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(ORDER_ITEMS)
                        .set(record)
                        .where(ORDER_ITEMS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o pedido")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(ORDER_ITEMS)
                    .set(record)
                    .returning(ORDER_ITEMS.ID, ORDER_ITEMS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o pedido")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
