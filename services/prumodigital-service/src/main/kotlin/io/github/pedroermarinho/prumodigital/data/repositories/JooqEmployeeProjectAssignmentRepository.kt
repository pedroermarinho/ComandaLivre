package io.github.pedroermarinho.prumodigital.repositories

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.EmployeeProjectAssignmentEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.project.EmployeeProjectAssignmentForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.EmployeeProjectAssignmentRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.EmployeeProjectAssignmentPersistenceMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import prumodigital.tables.references.EMPLOYEE_PROJECT_ASSIGNMENTS
import prumodigital.tables.references.PROJECTS
import java.util.*

@Repository
class JooqEmployeeProjectAssignmentRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val employeeProjectAssignmentPersistenceMapper: EmployeeProjectAssignmentPersistenceMapper,
) : EmployeeProjectAssignmentRepository {
    override fun create(form: EmployeeProjectAssignmentForm): Result<EntityId> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .insertInto(EMPLOYEE_PROJECT_ASSIGNMENTS)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.EMPLOYEE_ID, form.employeeId)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.PROJECT_ID, form.projectId)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.ROLE_IN_PROJECT_ID, form.roleInProjectId)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.ASSIGNMENT_START_DATE, form.assignmentStartDate)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.IS_ACTIVE_ASSIGNMENT, form.isActiveAssignment)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.IS_PROJECT_ADMIN, form.isProjectAdmin)
                .set(EMPLOYEE_PROJECT_ASSIGNMENTS.CREATED_BY, userAuth.sub)
                .returning(EMPLOYEE_PROJECT_ASSIGNMENTS.ID, EMPLOYEE_PROJECT_ASSIGNMENTS.PUBLIC_ID)
                .fetchOne()
                ?: return Result.failure(BusinessLogicException("Não foi possível criar a atribuição de funcionário ao projeto"))

        return Result.success(EntityId(result.id!!, result.publicId))
    }

    override fun getAll(
        pageable: PageableDTO,
        projectId: Int,
    ): Result<PageDTO<EmployeeProjectAssignmentEntity>> {
        var condition: Condition = DSL.trueCondition()
        condition = condition.and(EMPLOYEE_PROJECT_ASSIGNMENTS.DELETED_AT.isNull)
        condition = condition.and(EMPLOYEE_PROJECT_ASSIGNMENTS.PROJECT_ID.eq(projectId))

        if (pageable.search != null) {
            condition =
                condition.and(
                    EMPLOYEE_PROJECT_ASSIGNMENTS.EMPLOYEE_ID
                        .likeIgnoreCase("%${pageable.search}%"),
                )
        }

        val orderBy = getSortFields(pageable.sort, EMPLOYEE_PROJECT_ASSIGNMENTS).getOrNull()

        return runCatching {
            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                employeeProjectAssignmentPersistenceMapper.toEntity(it.into(EMPLOYEE_PROJECT_ASSIGNMENTS)).getOrThrow()
            }
        }
    }

    override fun getAll(): Result<List<EmployeeProjectAssignmentEntity>> {
        val result =
            query()
                .where(EMPLOYEE_PROJECT_ASSIGNMENTS.DELETED_AT.isNull)
                .fetch()

        return result
            .into(EMPLOYEE_PROJECT_ASSIGNMENTS)
            .map { employeeProjectAssignmentPersistenceMapper.toEntity(it).getOrThrow() }
            .let { Result.success(it) }
    }

    override fun getByProjectIdAndEmployeeId(
        projectId: Int,
        employeeId: Int,
    ): Result<EmployeeProjectAssignmentEntity> {
        val result =
            query()
                .where(PROJECTS.ID.eq(projectId))
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.EMPLOYEE_ID.eq(employeeId))
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Atribuição de funcionário ao projeto não encontrada"))

        return employeeProjectAssignmentPersistenceMapper.toEntity(result.into(EMPLOYEE_PROJECT_ASSIGNMENTS))
    }

    override fun getById(id: Int): Result<EmployeeProjectAssignmentEntity> {
        val result =
            query()
                .where(EMPLOYEE_PROJECT_ASSIGNMENTS.ID.eq(id))
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Atribuição de funcionário ao projeto não encontrada"))

        return employeeProjectAssignmentPersistenceMapper.toEntity(result.into(EMPLOYEE_PROJECT_ASSIGNMENTS))
    }

    override fun getById(id: UUID): Result<EmployeeProjectAssignmentEntity> {
        val result =
            query()
                .where(EMPLOYEE_PROJECT_ASSIGNMENTS.PUBLIC_ID.eq(id))
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Atribuição de funcionário ao projeto não encontrada"))

        return employeeProjectAssignmentPersistenceMapper.toEntity(result.into(EMPLOYEE_PROJECT_ASSIGNMENTS))
    }

    override fun existByProjectIdAndEmployeeId(
        projectId: Int,
        employeeId: Int,
    ): Boolean =
        dsl.fetchExists(
            dsl
                .selectFrom(EMPLOYEE_PROJECT_ASSIGNMENTS)
                .where(EMPLOYEE_PROJECT_ASSIGNMENTS.PROJECT_ID.eq(projectId))
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.EMPLOYEE_ID.eq(employeeId))
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.IS_ACTIVE_ASSIGNMENT.isTrue)
                .and(EMPLOYEE_PROJECT_ASSIGNMENTS.DELETED_AT.isNull),
        )

    private fun query() =
        dsl
            .select()
            .from(EMPLOYEE_PROJECT_ASSIGNMENTS)
            .innerJoin(PROJECTS)
            .on(EMPLOYEE_PROJECT_ASSIGNMENTS.PROJECT_ID.eq(PROJECTS.ID))

    override fun save(entity: EmployeeProjectAssignmentEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = employeeProjectAssignmentPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(EMPLOYEE_PROJECT_ASSIGNMENTS)
                        .set(record)
                        .where(EMPLOYEE_PROJECT_ASSIGNMENTS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a atribuição de funcionário ao projeto")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(EMPLOYEE_PROJECT_ASSIGNMENTS)
                    .set(record)
                    .returning(EMPLOYEE_PROJECT_ASSIGNMENTS.ID, EMPLOYEE_PROJECT_ASSIGNMENTS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a atribuição de funcionário ao projeto")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
