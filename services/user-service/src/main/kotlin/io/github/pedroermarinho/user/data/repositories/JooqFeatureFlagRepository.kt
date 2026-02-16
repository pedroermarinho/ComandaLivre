package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.FeatureFlagEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.repositories.FeatureFlagRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.FeatureFlagPersistenceMapper
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import shared.tables.references.FEATURE_FLAGS
import java.util.*

@Repository
class JooqFeatureFlagRepository(
    private val dsl: DSLContext,
    private val featureFlagPersistenceMapper: FeatureFlagPersistenceMapper,
) : FeatureFlagRepository {
    override fun getById(id: Int): Result<FeatureFlagEntity> {
        val result =
            query()
                .where(FEATURE_FLAGS.ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Feature flag não encontrada"))

        return featureFlagPersistenceMapper.toEntity(result.into(FEATURE_FLAGS))
    }

    override fun getById(publicId: UUID): Result<FeatureFlagEntity> {
        val result =
            query()
                .where(FEATURE_FLAGS.PUBLIC_ID.eq(publicId))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Feature flag não encontrada"))

        return featureFlagPersistenceMapper.toEntity(result.into(FEATURE_FLAGS))
    }

    override fun getAll(pageable: PageableDTO): Result<PageDTO<FeatureFlagEntity>> =
        runCatching {
            val condition = FEATURE_FLAGS.DELETED_AT.isNull

            val orderBy = getSortFields(pageable.sort, FEATURE_FLAGS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                featureFlagPersistenceMapper.toEntity(it.into(FEATURE_FLAGS)).getOrThrow()
            }
        }

    override fun isFeatureEnabled(keyFlag: String): Result<Boolean> {
        val result =
            query()
                .where(FEATURE_FLAGS.KEY_FLAG.eq(keyFlag))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Feature flag não encontrada"))

        return Result.success(result.into(FEATURE_FLAGS).enabled ?: false)
    }

    private fun query() =
        dsl
            .select()
            .from(FEATURE_FLAGS)

    override fun save(entity: FeatureFlagEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = featureFlagPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(FEATURE_FLAGS)
                        .set(record)
                        .where(FEATURE_FLAGS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a feature flag")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(FEATURE_FLAGS)
                    .set(record)
                    .returning(FEATURE_FLAGS.ID, FEATURE_FLAGS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a feature flag")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
