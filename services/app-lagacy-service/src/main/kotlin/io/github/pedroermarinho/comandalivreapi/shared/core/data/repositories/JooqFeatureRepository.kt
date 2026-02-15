package io.github.pedroermarinho.comandalivreapi.shared.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureFilterDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.FeatureRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.FeaturePersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.fetchPage
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.getSortFields
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import shared.tables.references.FEATURES_CATALOG
import shared.tables.references.FEATURE_GROUPS
import shared.tables.references.GROUP_FEATURE_PERMISSIONS
import java.util.*

@Repository
class JooqFeatureRepository(
    private val dsl: DSLContext,
    private val featurePersistenceMapper: FeaturePersistenceMapper,
) : FeatureRepository {
    override fun getByKey(key: String): Result<FeatureEntity> {
        val result =
            query()
                .where(FEATURES_CATALOG.FEATURE_KEY.eq(key))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Feature não encontrada"))

        return featurePersistenceMapper.toEntity(result.into(FEATURES_CATALOG))
    }

    override fun getById(id: UUID): Result<FeatureEntity> {
        val result =
            query()
                .where(FEATURES_CATALOG.PUBLIC_ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Feature não encontrada"))

        return featurePersistenceMapper.toEntity(result.into(FEATURES_CATALOG))
    }

    override fun getAll(
        pageable: PageableDTO,
        filter: FeatureFilterDTO,
    ): Result<PageDTO<FeatureEntity>> =
        runCatching {
            var condition = FEATURES_CATALOG.DELETED_AT.isNull

            val orderBy = getSortFields(pageable.sort, FEATURES_CATALOG).getOrNull()

            if (filter.group != null) {
                condition =
                    condition.and(
                        DSL.exists(
                            dsl
                                .selectOne()
                                .from(GROUP_FEATURE_PERMISSIONS)
                                .join(FEATURE_GROUPS)
                                .on(FEATURE_GROUPS.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID))
                                .where(FEATURE_GROUPS.PUBLIC_ID.eq(filter.group))
                                .and(GROUP_FEATURE_PERMISSIONS.FEATURE_ID.eq(FEATURES_CATALOG.ID))
                                .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.eq(true)),
                        ),
                    )
            }

            if (filter.excludeGroup != null) {
                condition =
                    condition.and(
                        DSL.notExists(
                            dsl
                                .selectOne()
                                .from(GROUP_FEATURE_PERMISSIONS)
                                .join(FEATURE_GROUPS)
                                .on(FEATURE_GROUPS.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID))
                                .where(FEATURE_GROUPS.PUBLIC_ID.eq(filter.excludeGroup))
                                .and(GROUP_FEATURE_PERMISSIONS.FEATURE_ID.eq(FEATURES_CATALOG.ID))
                                .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.eq(true)),
                        ),
                    )
            }

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                featurePersistenceMapper.toEntity(it.into(FEATURES_CATALOG)).getOrThrow()
            }
        }

    private fun query() =
        dsl
            .select()
            .from(FEATURES_CATALOG)

    override fun save(entity: FeatureEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = featurePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(FEATURES_CATALOG)
                        .set(record)
                        .where(FEATURES_CATALOG.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a feature")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(FEATURES_CATALOG)
                    .set(record)
                    .returning(FEATURES_CATALOG.ID, FEATURES_CATALOG.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a feature")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
