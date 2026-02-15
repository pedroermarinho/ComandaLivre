package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectStatusEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectStatusRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ProjectStatusPersistenceMapper
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
import prumodigital.tables.references.PROJECT_STATUS
import java.util.*

@Repository
class JooqProjectStatusRepository(
    private val dsl: DSLContext,
    private val projectStatusPersistenceMapper: ProjectStatusPersistenceMapper,
) : ProjectStatusRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectStatusEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(PROJECT_STATUS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        PROJECT_STATUS.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(PROJECT_STATUS.DESCRIPTION.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, PROJECT_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                projectStatusPersistenceMapper.toEntity(it.into(PROJECT_STATUS)).getOrThrow()
            }
        }

    override fun getAll(): Result<List<ProjectStatusEntity>> {
        val result =
            query()
                .where(PROJECT_STATUS.DELETED_AT.isNull)
                .fetch()
                .into(PROJECT_STATUS)

        return Result.success(result.map { projectStatusPersistenceMapper.toEntity(it).getOrThrow() })
    }

    override fun getById(id: UUID): Result<ProjectStatusEntity> {
        val result =
            query()
                .where(PROJECT_STATUS.PUBLIC_ID.eq(id))
                .and(PROJECT_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status do projeto não encontrado"))

        return projectStatusPersistenceMapper.toEntity(result.into(PROJECT_STATUS))
    }

    override fun getById(id: Int): Result<ProjectStatusEntity> {
        val result =
            query()
                .where(PROJECT_STATUS.ID.eq(id))
                .and(PROJECT_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status do projeto não encontrado"))

        return projectStatusPersistenceMapper.toEntity(result.into(PROJECT_STATUS))
    }

    override fun getByKey(key: String): Result<ProjectStatusEntity> {
        val result =
            query()
                .where(PROJECT_STATUS.KEY.eq(key))
                .and(PROJECT_STATUS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Status do projeto não encontrado"))

        return projectStatusPersistenceMapper.toEntity(result.into(PROJECT_STATUS))
    }

    private fun query() =
        dsl
            .select()
            .from(PROJECT_STATUS)

    override fun save(entity: ProjectStatusEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = projectStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(PROJECT_STATUS)
                        .set(record)
                        .where(PROJECT_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status do projeto")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(PROJECT_STATUS)
                    .set(record)
                    .returning(PROJECT_STATUS.ID, PROJECT_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status do projeto")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
