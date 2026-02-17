package io.github.pedroermarinho.company.repositories

import company.tables.references.COMPANIES
import company.tables.references.COMPANY_TYPES
import company.tables.references.EMPLOYEES
import company.tables.references.ROLE_TYPES
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeePersistenceMapper
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
import java.util.*

@Repository
class JooqEmployeeRepository(
    private val dsl: DSLContext,
    private val employeePersistenceMapper: EmployeePersistenceMapper,
) : EmployeeRepository {
    override fun getById(id: UUID): Result<EmployeeEntity> {
        val result =
            query()
                .where(EMPLOYEES.PUBLIC_ID.eq(id))
                .and(EMPLOYEES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("funcionário não encontrado"))

        return employeePersistenceMapper.toEntity(
            employeesRecord = result.into(EMPLOYEES),
            roleTypesRecord = result.into(ROLE_TYPES),
            companyTypesRecord = result.into(COMPANY_TYPES),
        )
    }

    override fun getById(id: Int): Result<EmployeeEntity> {
        val result =
            query()
                .where(EMPLOYEES.ID.eq(id))
                .and(EMPLOYEES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("funcionário não encontrado"))
        return employeePersistenceMapper.toEntity(
            employeesRecord = result.into(EMPLOYEES),
            roleTypesRecord = result.into(ROLE_TYPES),
            companyTypesRecord = result.into(COMPANY_TYPES),
        )
    }

    override fun getByUserId(
        pageable: PageableDTO,
        userId: Int,
    ): Result<PageDTO<EmployeeEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(EMPLOYEES.USER_ID.eq(userId))
            condition = condition.and(EMPLOYEES.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        COMPANIES.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(ROLE_TYPES.NAME.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, EMPLOYEES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                employeePersistenceMapper
                    .toEntity(
                        employeesRecord = it.into(EMPLOYEES),
                        roleTypesRecord = it.into(ROLE_TYPES),
                        companyTypesRecord = it.into(COMPANY_TYPES),
                    ).getOrThrow()
            }
        }

    override fun getByCompanyId(
        companyId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<EmployeeEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(COMPANIES.PUBLIC_ID.eq(companyId))
            condition = condition.and(EMPLOYEES.DELETED_AT.isNull)

            val orderBy = getSortFields(pageable.sort, EMPLOYEES).getOrNull()

            if (pageable.search != null) {
                condition =
                    condition.and(
                        ROLE_TYPES.NAME.likeIgnoreCase("%${pageable.search}%"),
                    )
            }

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                employeePersistenceMapper
                    .toEntity(
                        employeesRecord = it.into(EMPLOYEES),
                        roleTypesRecord = it.into(ROLE_TYPES),
                        companyTypesRecord = it.into(COMPANY_TYPES),
                    ).getOrThrow()
            }
        }

    override fun isEmployeeOfCompany(
        userId: Int,
        companyId: Int,
    ): Result<Boolean> {
        val result =
            dsl.fetchExists(
                dsl
                    .select(EMPLOYEES.ID)
                    .from(EMPLOYEES)
                    .where(EMPLOYEES.USER_ID.eq(userId))
                    .and(EMPLOYEES.COMPANY_ID.eq(companyId))
                    .and(EMPLOYEES.STATUS.eq(true))
                    .and(EMPLOYEES.DELETED_AT.isNull),
            )

        return Result.success(result)
    }

    override fun hasActiveCompanyRelation(userId: Int): Result<Boolean> {
        val result =
            dsl.fetchExists(
                dsl
                    .select(EMPLOYEES.ID)
                    .from(EMPLOYEES)
                    .where(EMPLOYEES.USER_ID.eq(userId))
                    .and(EMPLOYEES.STATUS.eq(true))
                    .and(EMPLOYEES.DELETED_AT.isNull),
            )

        return Result.success(result)
    }

    override fun getByCompanyId(
        companyId: Int,
        userId: Int,
    ): Result<EmployeeEntity> {
        val result =
            query()
                .where(EMPLOYEES.COMPANY_ID.eq(companyId))
                .and(EMPLOYEES.USER_ID.eq(userId))
                .and(EMPLOYEES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Funcionário não encontrado"))

        return employeePersistenceMapper.toEntity(
            employeesRecord = result.into(EMPLOYEES),
            roleTypesRecord = result.into(ROLE_TYPES),
            companyTypesRecord = result.into(COMPANY_TYPES),
        )
    }

    override fun getByUserIdAndCompanyId(
        userId: Int,
        companyId: Int,
    ): Result<EmployeeEntity> {
        val result =
            query()
                .where(EMPLOYEES.USER_ID.eq(userId))
                .and(EMPLOYEES.COMPANY_ID.eq(companyId))
                .and(EMPLOYEES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Funcionário não encontrado"))

        return employeePersistenceMapper.toEntity(
            employeesRecord = result.into(EMPLOYEES),
            roleTypesRecord = result.into(ROLE_TYPES),
            companyTypesRecord = result.into(COMPANY_TYPES),
        )
    }

    private fun query() =
        dsl
            .select()
            .from(EMPLOYEES)
            .innerJoin(COMPANIES)
            .on(EMPLOYEES.COMPANY_ID.eq(COMPANIES.ID))
            .innerJoin(ROLE_TYPES)
            .on(EMPLOYEES.ROLE_ID.eq(ROLE_TYPES.ID))
            .innerJoin(COMPANY_TYPES)
            .on(ROLE_TYPES.COMPANY_TYPE_ID.eq(COMPANY_TYPES.ID))

    override fun save(entity: EmployeeEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = employeePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(EMPLOYEES)
                        .set(record)
                        .where(EMPLOYEES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o funcionário")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(EMPLOYEES)
                    .set(record)
                    .returning(EMPLOYEES.ID, EMPLOYEES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o funcionário")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
