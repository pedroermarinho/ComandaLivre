package io.github.pedroermarinho.comandalivreapi.comandalivre.core.data.repositories

import comandalivre.tables.references.COMMAND_STATUS
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.CommandStatusRepository
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.CommandStatusPersistenceMapper
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
class JooqCommandStatusRepository(
    private val dsl: DSLContext,
    private val commandStatusPersistenceMapper: CommandStatusPersistenceMapper,
) : CommandStatusRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<CommandStatus>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(COMMAND_STATUS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(COMMAND_STATUS.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, COMMAND_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                commandStatusPersistenceMapper.toEntity(it.into(COMMAND_STATUS)).getOrThrow()
            }
        }

    override fun getById(statusId: Int): Result<CommandStatus> {
        val result =
            query()
                .where(COMMAND_STATUS.ID.eq(statusId))
                .fetchOne() ?: return Result.failure(NotFoundException("Status da comanda não encontrado"))
        return commandStatusPersistenceMapper.toEntity(result.into(COMMAND_STATUS))
    }

    override fun getByName(statusName: String): Result<CommandStatus> {
        val result =
            query()
                .where(COMMAND_STATUS.NAME.eq(statusName))
                .fetchOne() ?: return Result.failure(NotFoundException("Status da comanda não encontrado"))
        return commandStatusPersistenceMapper.toEntity(result.into(COMMAND_STATUS))
    }

    override fun getByKey(key: String): Result<CommandStatus> {
        val result =
            query()
                .where(COMMAND_STATUS.KEY.eq(key))
                .fetchOne() ?: return Result.failure(NotFoundException("Status da comanda não encontrado"))
        return commandStatusPersistenceMapper.toEntity(result.into(COMMAND_STATUS))
    }

    private fun query() =
        dsl
            .select()
            .from(COMMAND_STATUS)

    override fun save(entity: CommandStatus): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = commandStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(COMMAND_STATUS)
                        .set(record)
                        .where(COMMAND_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status da comanda")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(COMMAND_STATUS)
                    .set(record)
                    .returning(COMMAND_STATUS.ID, COMMAND_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status da comanda")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
