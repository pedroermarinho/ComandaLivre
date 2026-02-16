package io.github.pedroermarinho.comandalivre.repositories

import comandalivre.tables.references.COMMANDS
import comandalivre.tables.references.COMMAND_STATUS
import comandalivre.tables.references.TABLES
import company.tables.references.COMPANIES
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.CommandEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandPersistenceMapper
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
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqCommandRepository(
    private val dsl: DSLContext,
    private val commandPersistenceMapper: CommandPersistenceMapper,
) : CommandRepository {
    override fun getById(id: UUID): Result<CommandEntity> {
        val result =
            query().where(COMMANDS.PUBLIC_ID.eq(id)).fetchOne()
                ?: return Result.failure(NotFoundException("Comando não encontrado"))
        return commandPersistenceMapper.toEntity(commandsRecord = result.into(COMMANDS), commandStatusRecord = result.into(COMMAND_STATUS))
    }

    override fun getById(id: Int): Result<CommandEntity> {
        val result =
            query().where(COMMANDS.ID.eq(id)).fetchOne()
                ?: return Result.failure(NotFoundException("Comando não encontrado"))
        return commandPersistenceMapper.toEntity(commandsRecord = result.into(COMMANDS), commandStatusRecord = result.into(COMMAND_STATUS))
    }

    override fun getAll(
        pageable: PageableDTO,
        filter: CommandFilterDTO,
    ): Result<PageDTO<CommandEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(COMMANDS.DELETED_AT.isNull)

            if (filter.companyId != null) {
                condition = condition.and(COMPANIES.PUBLIC_ID.eq(filter.companyId))
            }

            if (filter.tableId != null) {
                condition = condition.and(TABLES.PUBLIC_ID.eq(filter.tableId))
            }

            if (filter.status != null && filter.status.isNotEmpty()) {
                val statusKeys = filter.status.map { it.value }
                condition = condition.and(COMMAND_STATUS.KEY.`in`(statusKeys))
            }

            if (pageable.search != null) {
                condition =
                    condition.and(
                        COMMANDS.COMMAND_NAME.likeIgnoreCase("%${pageable.search}%"),
                    )
            }

            val orderBy = getSortFields(pageable.sort, COMMANDS, pageable.direction).getOrNull()
            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                commandPersistenceMapper
                    .toEntity(
                        commandsRecord = it.into(COMMANDS),
                        commandStatusRecord = it.into(COMMAND_STATUS),
                    ).getOrThrow()
            }
        }

    override fun getAllList(
        companyId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        statusId: Int,
    ): Result<List<CommandEntity>> =
        runCatching {
            val results =
                query()
                    .where(COMMANDS.COMPANY_ID.eq(companyId))
                    .and(COMMANDS.CREATED_AT.between(startDate).and(endDate))
                    .and(COMMANDS.STATUS_ID.eq(statusId))
                    .and(COMMANDS.DELETED_AT.isNull)
                    .orderBy(COMMANDS.CREATED_AT.desc())
                    .fetch()

            results.map { commandPersistenceMapper.toEntity(commandsRecord = it.into(COMMANDS), commandStatusRecord = it.into(COMMAND_STATUS)).getOrThrow() }
        }

    override fun getIdById(id: UUID): Result<EntityId> {
        val result =
            dsl
                .select(COMMANDS.ID, COMMANDS.PUBLIC_ID)
                .from(COMMANDS)
                .where(COMMANDS.PUBLIC_ID.eq(id))
                .and(COMMANDS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Comando não encontrado"))
        return Result.success(EntityId(result[COMMANDS.ID]!!, result[COMMANDS.PUBLIC_ID]!!))
    }

    override fun count(): Result<Long> =
        runCatching {
            dsl.fetchCount(COMMANDS.where(COMMANDS.DELETED_AT.isNull)).toLong()
        }

    override fun existsByTableIdAndStatusIn(
        tableId: TableId,
        statusKeys: List<CommandStatusEnum>,
    ): Boolean {
        val condition =
            COMMANDS.TABLE_ID
                .eq(tableId.value)
                .and(COMMANDS.DELETED_AT.isNull)
                .and(COMMAND_STATUS.KEY.`in`(statusKeys.map { it.value }))
        return dsl.fetchExists(
            dsl
                .selectOne()
                .from(COMMANDS)
                .innerJoin(COMMAND_STATUS)
                .on(COMMANDS.STATUS_ID.eq(COMMAND_STATUS.ID))
                .where(condition),
        )
    }

    override fun exists(id: UUID): Boolean =
        dsl.fetchExists(
            dsl
                .selectOne()
                .from(COMMANDS)
                .where(COMMANDS.PUBLIC_ID.eq(id))
                .and(COMMANDS.DELETED_AT.isNull),
        )

    override fun save(entity: CommandEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = commandPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(COMMANDS)
                        .set(record)
                        .where(COMMANDS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a comanda")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(COMMANDS)
                    .set(record)
                    .returning(COMMANDS.ID, COMMANDS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a comanda")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(COMMANDS)
            .innerJoin(TABLES)
            .on(COMMANDS.TABLE_ID.eq(TABLES.ID))
            .innerJoin(COMPANIES)
            .on(TABLES.COMPANY_ID.eq(COMPANIES.ID))
            .innerJoin(COMMAND_STATUS)
            .on(COMMANDS.STATUS_ID.eq(COMMAND_STATUS.ID))
}
