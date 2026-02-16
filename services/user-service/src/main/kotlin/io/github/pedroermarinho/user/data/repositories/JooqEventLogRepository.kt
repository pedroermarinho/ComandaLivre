package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.EventLogEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.repositories.EventLogRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.EventLogPersistenceMapper
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import shared.tables.references.EVENT_LOG
import shared.tables.references.USERS

@Repository
class JooqEventLogRepository(
    private val dsl: DSLContext,
    private val eventLogPersistenceMapper: EventLogPersistenceMapper,
) : EventLogRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<EventLogEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()

            if (pageable.search != null) {
                condition =
                    condition.and(
                        EVENT_LOG.EVENT_TITLE
                            .likeIgnoreCase("${pageable.search}%"),
                    )
            }

            val orderBy = getSortFields(pageable.sort, USERS).getOrNull()

            val query =
                dsl
                    .select()
                    .from(EVENT_LOG)

            fetchPage(
                dsl = dsl,
                baseQuery = query,
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                eventLogPersistenceMapper.toEntity(it.into(EVENT_LOG)).getOrThrow()
            }
        }

    override fun save(entity: EventLogEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = eventLogPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(EVENT_LOG)
                        .set(record)
                        .where(EVENT_LOG.ID.eq(entity.id.internalId.toLong()))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o log de evento")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(EVENT_LOG)
                    .set(record)
                    .returning(EVENT_LOG.ID, EVENT_LOG.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o log de evento")

            return@runCatching EntityId(result.id!!.toInt(), result.publicId)
        }
}
