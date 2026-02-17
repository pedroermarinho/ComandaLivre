package io.github.pedroermarinho.comandalivre.data.repositories

import comandalivre.tables.references.TABLE_STATUS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableStatusTypeRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TableStatusPersistenceMapper
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

@Repository
class JooqTableStatusTypeRepository(
    private val dsl: DSLContext,
    private val tableStatusPersistenceMapper: TableStatusPersistenceMapper,
) : TableStatusTypeRepository {
    override fun getById(statusId: Int): Result<TableStatus> {
        val result =
            dsl
                .select()
                .from(TABLE_STATUS)
                .where(TABLE_STATUS.ID.eq(statusId))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o status da mesa com o id $statusId"))
        return tableStatusPersistenceMapper.toEntity(result.into(TABLE_STATUS))
    }

    override fun getByName(statusName: String): Result<TableStatus> {
        val result =
            dsl
                .select()
                .from(TABLE_STATUS)
                .where(TABLE_STATUS.NAME.eq(statusName))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o status da mesa com o nome $statusName"))
        return tableStatusPersistenceMapper.toEntity(result.into(TABLE_STATUS))
    }

    override fun getByKey(key: String): Result<TableStatus> {
        val result =
            dsl
                .select()
                .from(TABLE_STATUS)
                .where(TABLE_STATUS.KEY.eq(key))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o status da mesa com a chave $key"))

        return tableStatusPersistenceMapper.toEntity(result.into(TABLE_STATUS))
    }

    override fun getAll(pageable: PageableDTO): Result<PageDTO<TableStatus>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(TABLE_STATUS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(TABLE_STATUS.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, TABLE_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                tableStatusPersistenceMapper.toEntity(it.into(TABLE_STATUS)).getOrThrow()
            }
        }

    override fun save(entity: TableStatus): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = tableStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(TABLE_STATUS)
                        .set(record)
                        .where(TABLE_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status da mesa")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(TABLE_STATUS)
                    .set(record)
                    .returning(TABLE_STATUS.ID, TABLE_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status da mesa")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(TABLE_STATUS)
}
