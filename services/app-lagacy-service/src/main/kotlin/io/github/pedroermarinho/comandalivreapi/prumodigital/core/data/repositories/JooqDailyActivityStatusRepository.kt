package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyActivityStatusEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyActivityStatusPersistenceMapper
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
import prumodigital.tables.references.DAILY_ACTIVITY_STATUS
import java.util.*

@Repository
class JooqDailyActivityStatusRepository(
    private val dsl: DSLContext,
    private val dailyActivityStatusPersistenceMapper: DailyActivityStatusPersistenceMapper,
) : DailyActivityStatusRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<DailyActivityStatusEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(DAILY_ACTIVITY_STATUS.DELETED_AT.isNull)

            val orderBy = getSortFields(pageable.sort, DAILY_ACTIVITY_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                dailyActivityStatusPersistenceMapper.toEntity(it.into(DAILY_ACTIVITY_STATUS)).getOrThrow()
            }
        }

    override fun getAll(): Result<List<DailyActivityStatusEntity>> {
        val result =
            query()
                .where(DAILY_ACTIVITY_STATUS.DELETED_AT.isNull)
                .fetch()
                .into(DAILY_ACTIVITY_STATUS)

        return Result.success(result.map { dailyActivityStatusPersistenceMapper.toEntity(it).getOrThrow() })
    }

    override fun getById(id: UUID): Result<DailyActivityStatusEntity> {
        val result =
            query()
                .where(DAILY_ACTIVITY_STATUS.PUBLIC_ID.eq(id))
                .and(DAILY_ACTIVITY_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status de atividade diária não encontrado"))

        return dailyActivityStatusPersistenceMapper.toEntity(result.into(DAILY_ACTIVITY_STATUS))
    }

    override fun getById(id: Int): Result<DailyActivityStatusEntity> {
        val result =
            query()
                .where(DAILY_ACTIVITY_STATUS.ID.eq(id))
                .and(DAILY_ACTIVITY_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status de atividade diária não encontrado"))

        return dailyActivityStatusPersistenceMapper.toEntity(result.into(DAILY_ACTIVITY_STATUS))
    }

    override fun getByKey(key: String): Result<DailyActivityStatusEntity> {
        val result =
            query()
                .where(DAILY_ACTIVITY_STATUS.KEY.eq(key))
                .and(DAILY_ACTIVITY_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status de atividade diária não encontrado"))

        return dailyActivityStatusPersistenceMapper.toEntity(result.into(DAILY_ACTIVITY_STATUS))
    }

    private fun query() =
        dsl
            .select()
            .from(DAILY_ACTIVITY_STATUS)

    override fun save(entity: DailyActivityStatusEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = dailyActivityStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(DAILY_ACTIVITY_STATUS)
                        .set(record)
                        .where(DAILY_ACTIVITY_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status da atividade diária")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(DAILY_ACTIVITY_STATUS)
                    .set(record)
                    .returning(DAILY_ACTIVITY_STATUS.ID, DAILY_ACTIVITY_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status da atividade diária")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
