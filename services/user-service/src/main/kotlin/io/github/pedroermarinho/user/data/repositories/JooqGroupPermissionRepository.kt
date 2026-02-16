package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.FeatureEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.forms.user.GrantPermissionForm
import io.github.pedroermarinho.user.domain.repositories.GroupPermissionRepository
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.user.infra.mappers.FeaturePersistenceMapper
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.springframework.stereotype.Repository
import shared.tables.references.FEATURES_CATALOG
import shared.tables.references.FEATURE_GROUPS
import shared.tables.references.GROUP_FEATURE_PERMISSIONS
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqGroupPermissionRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val featurePersistenceMapper: FeaturePersistenceMapper,
) : GroupPermissionRepository {
    override fun create(form: GrantPermissionForm): Result<Unit> {
        val currentUserSub =
            currentUserService.getLoggedUser().fold(
                onSuccess = { it.sub },
                onFailure = { null },
            )

        val rowsUpdated =
            dsl
                .insertInto(GROUP_FEATURE_PERMISSIONS)
                .set(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID, form.groupId)
                .set(GROUP_FEATURE_PERMISSIONS.FEATURE_ID, form.featureId)
                .set(GROUP_FEATURE_PERMISSIONS.CREATED_AT, LocalDateTime.now())
                .set(GROUP_FEATURE_PERMISSIONS.UPDATED_AT, LocalDateTime.now())
                .set(GROUP_FEATURE_PERMISSIONS.CREATED_BY, currentUserSub)
                .set(GROUP_FEATURE_PERMISSIONS.VERSION, 1)
                .execute()

        if (rowsUpdated == 0) {
            return Result.failure(BusinessLogicException("Falha ao registrar a permissão de grupo de funcionalidades no banco de dados."))
        }

        return Result.success(Unit)
    }

    override fun checkFeatureInGroup(
        featureId: Int,
        featureGroupId: Int,
    ): Boolean =
        dsl.fetchExists(
            query()
                .where(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID.eq(featureGroupId))
                .and(GROUP_FEATURE_PERMISSIONS.FEATURE_ID.eq(featureId))
                .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.eq(true)),
        )

    override fun getAll(
        groupId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<FeatureEntity>> =
        runCatching {
            val condition =
                GROUP_FEATURE_PERMISSIONS.DELETED_AT.isNull
                    .and(FEATURE_GROUPS.PUBLIC_ID.eq(groupId))
                    .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.isTrue)

            val orderBy = getSortFields(pageable.sort, GROUP_FEATURE_PERMISSIONS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = queryFeature(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                featurePersistenceMapper.toEntity(it.into(FEATURES_CATALOG)).getOrThrow()
            }
        }

    override fun exists(
        featureId: Int,
        groupId: Int,
    ): Boolean =
        dsl.fetchExists(
            query()
                .where(GROUP_FEATURE_PERMISSIONS.FEATURE_ID.eq(featureId))
                .and(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID.eq(groupId)),
        )

    override fun changeEnabled(
        featureId: Int,
        groupId: Int,
        enabled: Boolean,
    ): Result<Unit> {
        val currentUserSub =
            currentUserService.getLoggedUser().fold(
                onSuccess = { it.sub },
                onFailure = { null },
            )

        val result =
            dsl
                .update(GROUP_FEATURE_PERMISSIONS)
                .set(GROUP_FEATURE_PERMISSIONS.IS_ENABLED, enabled)
                .set(GROUP_FEATURE_PERMISSIONS.UPDATED_AT, LocalDateTime.now())
                .set(GROUP_FEATURE_PERMISSIONS.UPDATED_BY, currentUserSub)
                .where(GROUP_FEATURE_PERMISSIONS.FEATURE_ID.eq(featureId))
                .and(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID.eq(groupId))
                .execute()

        return if (result > 0) Result.success(Unit) else Result.failure(BusinessLogicException("Falha ao remover permissão do grupo de funcionalidades."))
    }

    private fun query() =
        dsl
            .select()
            .from(GROUP_FEATURE_PERMISSIONS)

    private fun queryFeature(): SelectJoinStep<Record> =
        dsl
            .select()
            .from(FEATURES_CATALOG)
            .join(GROUP_FEATURE_PERMISSIONS)
            .on(FEATURES_CATALOG.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_ID))
            .join(FEATURE_GROUPS)
            .on(FEATURE_GROUPS.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID))
}
