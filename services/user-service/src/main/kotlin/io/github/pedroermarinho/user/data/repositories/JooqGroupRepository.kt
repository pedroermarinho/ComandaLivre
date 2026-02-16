package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.GroupEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.repositories.GroupRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.GroupPersistenceMapper
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import shared.tables.references.FEATURES_CATALOG
import shared.tables.references.FEATURE_GROUPS
import java.util.*

@Repository
class JooqGroupRepository(
    private val dsl: DSLContext,
    private val groupPersistenceMapper: GroupPersistenceMapper,
) : GroupRepository {
    override fun getByKey(key: String): Result<GroupEntity> {
        val result =
            query()
                .where(FEATURE_GROUPS.GROUP_KEY.eq(key))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Grupo de funcionalidades não encontrado"))

        return groupPersistenceMapper.toEntity(result.into(FEATURE_GROUPS))
    }

    override fun getById(id: UUID): Result<GroupEntity> {
        val result =
            query()
                .where(FEATURE_GROUPS.PUBLIC_ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Grupo de funcionalidades não encontrado"))

        return groupPersistenceMapper.toEntity(result.into(FEATURE_GROUPS))
    }

    override fun getById(id: Int): Result<GroupEntity> {
        val result =
            query()
                .where(FEATURE_GROUPS.ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Grupo de funcionalidades não encontrado"))

        return groupPersistenceMapper.toEntity(result.into(FEATURE_GROUPS))
    }

    override fun getAll(pageable: PageableDTO): Result<PageDTO<GroupEntity>> =
        runCatching {
            val condition = FEATURE_GROUPS.DELETED_AT.isNull

            val orderBy = getSortFields(pageable.sort, FEATURES_CATALOG).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                groupPersistenceMapper.toEntity(it.into(FEATURE_GROUPS)).getOrThrow()
            }
        }

    override fun getIdByPublicId(id: UUID): Result<EntityId> {
        val result =
            dsl
                .select(FEATURE_GROUPS.ID, FEATURE_GROUPS.PUBLIC_ID)
                .from(FEATURE_GROUPS)
                .where(FEATURE_GROUPS.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Grupo de funcionalidades não encontrado"))

        return Result.success(EntityId(result[FEATURE_GROUPS.ID]!!, result[FEATURE_GROUPS.PUBLIC_ID]!!))
    }

    private fun query() =
        dsl
            .select()
            .from(FEATURE_GROUPS)

    override fun save(entity: GroupEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = groupPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(FEATURE_GROUPS)
                        .set(record)
                        .where(FEATURE_GROUPS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o grupo")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(FEATURE_GROUPS)
                    .set(record)
                    .returning(FEATURE_GROUPS.ID, FEATURE_GROUPS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o grupo")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
