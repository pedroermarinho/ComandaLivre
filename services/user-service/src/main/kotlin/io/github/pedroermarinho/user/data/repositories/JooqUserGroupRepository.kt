package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.user.domain.forms.user.AssignUserToGroupForm
import io.github.pedroermarinho.user.domain.repositories.UserGroupRepository
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import user.tables.references.FEATURES_CATALOG
import user.tables.references.FEATURE_GROUPS
import user.tables.references.GROUP_FEATURE_PERMISSIONS
import user.tables.references.USER_FEATURE_GROUPS
import java.time.LocalDateTime

@Repository
class JooqUserGroupRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
) : UserGroupRepository {
    override fun create(form: AssignUserToGroupForm): Result<Unit> {
        val currentUserSub =
            currentUserService.getLoggedUser().fold(
                onSuccess = { it.sub },
                onFailure = { null },
            )

        val rowsUpdated =
            dsl
                .insertInto(USER_FEATURE_GROUPS)
                .set(USER_FEATURE_GROUPS.USER_ID, form.userId)
                .set(USER_FEATURE_GROUPS.FEATURE_GROUP_ID, form.featureGroupId)
                .set(USER_FEATURE_GROUPS.NOTES, form.notes)
                .set(USER_FEATURE_GROUPS.EXPIRES_AT, form.expiresAt)
                .set(USER_FEATURE_GROUPS.CREATED_AT, LocalDateTime.now())
                .set(USER_FEATURE_GROUPS.UPDATED_AT, LocalDateTime.now())
                .set(USER_FEATURE_GROUPS.CREATED_BY, currentUserSub)
                .set(USER_FEATURE_GROUPS.VERSION, 1)
                .execute()

        if (rowsUpdated == 0) {
            return Result.failure(BusinessLogicException("Falha ao registrar a permissão de grupo de funcionalidades no banco de dados."))
        }

        return Result.success(Unit)
    }

    override fun checkUserInGroup(
        userId: Int,
        featureGroupId: Int,
    ): Boolean =
        dsl.fetchExists(
            query()
                .where(USER_FEATURE_GROUPS.USER_ID.eq(userId))
                .and(FEATURE_GROUPS.ID.eq(featureGroupId))
                .and(USER_FEATURE_GROUPS.IS_ACTIVE.eq(true)),
        )

    override fun exists(
        userId: Int,
        featureGroupId: Int,
    ): Boolean =
        dsl.fetchExists(
            query()
                .where(USER_FEATURE_GROUPS.USER_ID.eq(userId))
                .and(USER_FEATURE_GROUPS.FEATURE_GROUP_ID.eq(featureGroupId)),
        )

    override fun hasAllPermissions(
        id: Int,
        features: List<String>,
    ): Boolean =
        dsl.fetchExists(
            queryCounted()
                .where(USER_FEATURE_GROUPS.USER_ID.eq(id))
                .and(FEATURES_CATALOG.FEATURE_KEY.`in`(features))
                .and(USER_FEATURE_GROUPS.IS_ACTIVE.eq(true))
                .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.eq(true))
                .groupBy(USER_FEATURE_GROUPS.USER_ID)
                .having(DSL.countDistinct(FEATURES_CATALOG.FEATURE_KEY).eq(features.size)),
        )

    override fun hasAnyPermission(
        id: Int,
        features: List<String>,
    ): Boolean =
        dsl.fetchExists(
            query()
                .where(USER_FEATURE_GROUPS.USER_ID.eq(id))
                .and(FEATURES_CATALOG.FEATURE_KEY.`in`(features))
                .and(USER_FEATURE_GROUPS.IS_ACTIVE.eq(true))
                .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.eq(true)),
        )

    override fun getFeatureKeysByUserId(userId: Int): Result<List<String>> {
        val featureKeys =
            dsl
                .select(FEATURES_CATALOG.FEATURE_KEY)
                .from(USER_FEATURE_GROUPS)
                .innerJoin(FEATURE_GROUPS)
                .on(FEATURE_GROUPS.ID.eq(USER_FEATURE_GROUPS.FEATURE_GROUP_ID))
                .innerJoin(GROUP_FEATURE_PERMISSIONS)
                .on(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID.eq(FEATURE_GROUPS.ID))
                .innerJoin(FEATURES_CATALOG)
                .on(FEATURES_CATALOG.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_ID))
                .where(USER_FEATURE_GROUPS.USER_ID.eq(userId))
                .and(USER_FEATURE_GROUPS.IS_ACTIVE.eq(true))
                .and(GROUP_FEATURE_PERMISSIONS.IS_ENABLED.eq(true))
                .fetch()
                .map { it.value1() }

        return Result.success(featureKeys)
    }

    override fun changeEnabled(
        userId: Int,
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
                .update(USER_FEATURE_GROUPS)
                .set(USER_FEATURE_GROUPS.IS_ACTIVE, enabled)
                .set(USER_FEATURE_GROUPS.UPDATED_AT, LocalDateTime.now())
                .set(USER_FEATURE_GROUPS.UPDATED_BY, currentUserSub)
                .where(USER_FEATURE_GROUPS.USER_ID.eq(userId))
                .and(USER_FEATURE_GROUPS.FEATURE_GROUP_ID.eq(groupId))
                .execute()

        return if (result > 0) Result.success(Unit) else Result.failure(BusinessLogicException("Falha ao remover o usuário do grupo."))
    }

    private fun query() =
        dsl
            .select()
            .from(USER_FEATURE_GROUPS)
            .innerJoin(FEATURE_GROUPS)
            .on(FEATURE_GROUPS.ID.eq(USER_FEATURE_GROUPS.FEATURE_GROUP_ID))
            .innerJoin(GROUP_FEATURE_PERMISSIONS)
            .on(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID.eq(FEATURE_GROUPS.ID))
            .innerJoin(FEATURES_CATALOG)
            .on(FEATURES_CATALOG.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_ID))

    private fun queryCounted() =
        dsl
            .select(USER_FEATURE_GROUPS.USER_ID)
            .from(USER_FEATURE_GROUPS)
            .innerJoin(FEATURE_GROUPS)
            .on(FEATURE_GROUPS.ID.eq(USER_FEATURE_GROUPS.FEATURE_GROUP_ID))
            .innerJoin(GROUP_FEATURE_PERMISSIONS)
            .on(GROUP_FEATURE_PERMISSIONS.FEATURE_GROUP_ID.eq(FEATURE_GROUPS.ID))
            .innerJoin(FEATURES_CATALOG)
            .on(FEATURES_CATALOG.ID.eq(GROUP_FEATURE_PERMISSIONS.FEATURE_ID))
}
