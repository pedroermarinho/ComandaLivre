package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.TABLES
import comandalivre.tables.references.TABLE_STATUS
import company.tables.references.COMPANIES
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.TablePersistenceMapper
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
import java.util.*

@Repository
class JooqTableRepository(
    private val dsl: DSLContext,
    private val tablePersistenceMapper: TablePersistenceMapper,
) : TableRepository {
    override fun getAll(
        pageable: PageableDTO,
        companyId: Int,
    ): Result<PageDTO<TableEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(TABLES.DELETED_AT.isNull)
            condition = condition.and(TABLES.COMPANY_ID.eq(companyId))

            if (pageable.search != null) {
                condition =
                    condition.and(
                        TABLES.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(TABLES.DESCRIPTION.likeIgnoreCase("%${pageable.search}%")),
                    )
            }
            val orderBy = getSortFields(pageable.sort, TABLES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                tablePersistenceMapper.toEntity(tablesRecord = it.into(TABLES), tableStatusRecord = it.into(TABLE_STATUS)).getOrThrow()
            }
        }

    override fun getAllList(companyId: Int): Result<List<TableEntity>> =
        runCatching {
            val results =
                query()
                    .where(TABLES.COMPANY_ID.eq(companyId))
                    .and(TABLES.DELETED_AT.isNull)
                    .orderBy(TABLES.NAME.asc())
                    .fetch()
            results.mapNotNull { tablePersistenceMapper.toEntity(tablesRecord = it.into(TABLES), tableStatusRecord = it.into(TABLE_STATUS)).getOrThrow() }
        }

    override fun getById(tablePublicId: UUID): Result<TableEntity> {
        val result =
            query()
                .where(TABLES.PUBLIC_ID.eq(tablePublicId))
                .and(TABLES.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Mesa não encontrada"))
        return tablePersistenceMapper.toEntity(tablesRecord = result.into(TABLES), tableStatusRecord = result.into(TABLE_STATUS))
    }

    override fun getById(id: Int): Result<TableEntity> {
        val result =
            query()
                .where(TABLES.ID.eq(id))
                .and(TABLES.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Mesa não encontrada"))
        return tablePersistenceMapper.toEntity(tablesRecord = result.into(TABLES), tableStatusRecord = result.into(TABLE_STATUS))
    }

    override fun getByIdUnsafe(id: Int): Result<TableEntity> {
        val result =
            query()
                .where(TABLES.ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Mesa não encontrada"))
        return tablePersistenceMapper.toEntity(tablesRecord = result.into(TABLES), tableStatusRecord = result.into(TABLE_STATUS))
    }

    override fun existsById(id: UUID): Boolean =
        dsl.fetchExists(
            dsl
                .selectFrom(TABLES)
                .where(TABLES.PUBLIC_ID.eq(id))
                .and(TABLES.DELETED_AT.isNull),
        )

    override fun existsByNameAndCompanyId(
        name: String,
        companyId: Int,
    ): Boolean =
        dsl.fetchExists(
            dsl
                .selectOne()
                .from(TABLES)
                .innerJoin(COMPANIES)
                .on(COMPANIES.ID.eq(TABLES.COMPANY_ID))
                .where(TABLES.NAME.eq(name))
                .and(COMPANIES.ID.eq(companyId))
                .and(TABLES.DELETED_AT.isNull),
        )

    override fun save(entity: TableEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = tablePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(TABLES)
                        .set(record)
                        .where(TABLES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a mesa")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(TABLES)
                    .set(record)
                    .returning(TABLES.ID, TABLES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a mesa")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(TABLES)
            .innerJoin(TABLE_STATUS)
            .on(TABLES.STATUS_ID.eq(TABLE_STATUS.ID))
}
