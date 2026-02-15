package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.dtos.ProjectCountByStatusDTO
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ProjectEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.project.ProjectCreateForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ProjectRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ProjectPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.fetchPage
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import prumodigital.tables.references.PROJECTS
import prumodigital.tables.references.PROJECT_STATUS
import java.util.*
import kotlin.text.get

@Repository
class JooqProjectRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val projectPersistenceMapper: ProjectPersistenceMapper,
) : ProjectRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<ProjectEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(PROJECTS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        PROJECTS.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(PROJECTS.DESCRIPTION.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, PROJECTS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                projectPersistenceMapper.toEntity(it.into(PROJECTS)).getOrThrow()
            }
        }

    override fun create(form: ProjectCreateForm): Result<EntityId> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .insertInto(PROJECTS)
                .set(PROJECTS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                .set(PROJECTS.COMPANY_ID, form.companyId)
                .set(PROJECTS.NAME, form.name)
                .set(PROJECTS.CODE, form.code)
                .set(PROJECTS.ADDRESS_ID, form.addressId)
                .set(PROJECTS.PLANNED_START_DATE, form.plannedStartDate)
                .set(PROJECTS.PLANNED_END_DATE, form.plannedEndDate)
                .set(PROJECTS.ACTUAL_START_DATE, form.actualStartDate)
                .set(PROJECTS.ACTUAL_END_DATE, form.actualEndDate)
                .set(PROJECTS.CLIENT_NAME, form.clientName)
                .set(PROJECTS.PROJECT_STATUS_ID, form.projectStatusId)
                .set(PROJECTS.BUDGET, form.budget)
                .set(PROJECTS.DESCRIPTION, form.description)
                .set(PROJECTS.CREATED_BY, userAuth.sub)
                .returning(PROJECTS.ID, PROJECTS.PUBLIC_ID)
                .fetchOne()
                ?: return Result.failure(BusinessLogicException("Não foi possível criar o projeto"))

        return Result.success(EntityId(result.id!!, result.publicId))
    }

    override fun getById(id: UUID): Result<ProjectEntity> {
        val result =
            query()
                .where(PROJECTS.PUBLIC_ID.eq(id))
                .and(PROJECTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Projeto não encontrado"))

        return projectPersistenceMapper.toEntity(result.into(PROJECTS))
    }

    override fun getById(id: Int): Result<ProjectEntity> {
        val result =
            query()
                .where(PROJECTS.ID.eq(id))
                .and(PROJECTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Projeto não encontrado"))

        return projectPersistenceMapper.toEntity(result.into(PROJECTS))
    }

    override fun getByCodeAndCompanyId(
        code: String,
        companyId: Int,
    ): Result<ProjectEntity> {
        val result =
            query()
                .where(PROJECTS.CODE.eq(code))
                .and(PROJECTS.COMPANY_ID.eq(companyId))
                .and(PROJECTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Projeto não encontrado"))

        return projectPersistenceMapper.toEntity(result.into(PROJECTS))
    }

    override fun updateStatus(
        publicId: UUID,
        statusId: Int,
    ): Result<Unit> {
        val user = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .update(PROJECTS)
                .set(PROJECTS.PROJECT_STATUS_ID, statusId)
                .set(PROJECTS.UPDATED_BY, user.sub)
                .where(PROJECTS.PUBLIC_ID.eq(publicId))
                .and(PROJECTS.DELETED_AT.isNull)
                .execute()

        if (result == 0) {
            return Result.failure(BusinessLogicException("Não foi possível atualizar o status do projeto"))
        }

        return Result.success(Unit)
    }

    override fun update(
        publicId: UUID,
        form: ProjectCreateForm,
    ): Result<Unit> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .update(PROJECTS)
                .set(PROJECTS.NAME, form.name)
                .set(PROJECTS.CODE, form.code)
                .set(PROJECTS.ADDRESS_ID, form.addressId)
                .set(PROJECTS.PLANNED_START_DATE, form.plannedStartDate)
                .set(PROJECTS.PLANNED_END_DATE, form.plannedEndDate)
                .set(PROJECTS.ACTUAL_START_DATE, form.actualStartDate)
                .set(PROJECTS.ACTUAL_END_DATE, form.actualEndDate)
                .set(PROJECTS.CLIENT_NAME, form.clientName)
                .set(PROJECTS.PROJECT_STATUS_ID, form.projectStatusId)
                .set(PROJECTS.BUDGET, form.budget)
                .set(PROJECTS.DESCRIPTION, form.description)
                .set(PROJECTS.UPDATED_BY, userAuth.sub)
                .where(PROJECTS.PUBLIC_ID.eq(publicId))
                .and(PROJECTS.DELETED_AT.isNull)
                .execute()

        if (result == 0) {
            return Result.failure(BusinessLogicException("Não foi possível atualizar o projeto"))
        }

        return Result.success(Unit)
    }

    override fun getProjectCountByStatus(): Result<List<ProjectCountByStatusDTO>> =
        runCatching {
            dsl
                .select(
                    PROJECT_STATUS.PUBLIC_ID.`as`("statusId"),
                    PROJECT_STATUS.NAME.`as`("statusName"),
                    DSL.count().`as`("projectCount"),
                ).from(PROJECTS)
                .join(PROJECT_STATUS)
                .on(PROJECTS.PROJECT_STATUS_ID.eq(PROJECT_STATUS.ID))
                .where(PROJECTS.DELETED_AT.isNull)
                .groupBy(PROJECTS.PROJECT_STATUS_ID, PROJECT_STATUS.NAME, PROJECT_STATUS.PUBLIC_ID)
                .fetch { record ->
                    ProjectCountByStatusDTO(
                        statusId = record["statusId", UUID::class.java],
                        statusName = record["statusName", String::class.java],
                        projectCount = record["projectCount", Int::class.java],
                    )
                }
        }

    private fun query() =
        dsl
            .select()
            .from(PROJECTS)

    override fun save(entity: ProjectEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = projectPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(PROJECTS)
                        .set(record)
                        .where(PROJECTS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o projeto")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(PROJECTS)
                    .set(record)
                    .returning(PROJECTS.ID, PROJECTS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o projeto")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
