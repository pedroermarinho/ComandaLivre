package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.WeatherStatusEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.WeatherStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.WeatherStatusPersistenceMapper
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
import prumodigital.tables.references.WEATHER_STATUS
import java.util.*

@Repository
class JooqWeatherStatusRepository(
    private val dsl: DSLContext,
    private val weatherStatusPersistenceMapper: WeatherStatusPersistenceMapper,
) : WeatherStatusRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<WeatherStatusEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(WEATHER_STATUS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        WEATHER_STATUS.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(WEATHER_STATUS.DESCRIPTION.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, WEATHER_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                weatherStatusPersistenceMapper.toEntity(it.into(WEATHER_STATUS)).getOrThrow()
            }
        }

    override fun getAll(): Result<List<WeatherStatusEntity>> {
        val result =
            query()
                .where(WEATHER_STATUS.DELETED_AT.isNull)
                .fetch()
                .into(WEATHER_STATUS)

        return Result.success(result.map { weatherStatusPersistenceMapper.toEntity(it).getOrThrow() })
    }

    override fun getById(id: UUID): Result<WeatherStatusEntity> {
        val result =
            query()
                .where(WEATHER_STATUS.PUBLIC_ID.eq(id))
                .and(WEATHER_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status do clima não encontrado"))

        return weatherStatusPersistenceMapper.toEntity(result.into(WEATHER_STATUS))
    }

    override fun getById(id: Int): Result<WeatherStatusEntity> {
        val result =
            query()
                .where(WEATHER_STATUS.ID.eq(id))
                .and(WEATHER_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status do clima não encontrado"))

        return weatherStatusPersistenceMapper.toEntity(result.into(WEATHER_STATUS))
    }

    override fun getByKey(key: String): Result<WeatherStatusEntity> {
        val result =
            query()
                .where(WEATHER_STATUS.KEY.eq(key))
                .and(WEATHER_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status do clima não encontrado"))

        return weatherStatusPersistenceMapper.toEntity(result.into(WEATHER_STATUS))
    }

    private fun query() =
        dsl
            .select()
            .from(WEATHER_STATUS)

    override fun save(entity: WeatherStatusEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = weatherStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(WEATHER_STATUS)
                        .set(record)
                        .where(WEATHER_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status do clima")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(WEATHER_STATUS)
                    .set(record)
                    .returning(WEATHER_STATUS.ID, WEATHER_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status do clima")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
