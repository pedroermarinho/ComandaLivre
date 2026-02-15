package io.github.pedroermarinho.comandalivreapi.company.core.data.repositories

import company.tables.references.COMPANIES
import company.tables.references.COMPANY_TYPES
import company.tables.references.EMPLOYEE_INVITES
import company.tables.references.EMPLOYEE_INVITE_STATUS
import company.tables.references.ROLE_TYPES
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.EmployeeInviteEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInvitePersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.fetchPage
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.getSortFields
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import shared.tables.references.USERS
import java.util.*

@Repository
class JooqEmployeeInviteRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val employeeInvitePersistenceMapper: EmployeeInvitePersistenceMapper,
) : EmployeeInviteRepository {
    override fun getById(id: UUID): Result<EmployeeInviteEntity> {
        val result =
            query()
                .where(EMPLOYEE_INVITES.PUBLIC_ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Convite não encontrado"))
        return employeeInvitePersistenceMapper.toEntity(
            employeeInvitesRecord = result.into(EMPLOYEE_INVITES),
            roleTypesRecord = result.into(ROLE_TYPES),
            employeeInvitesStatusRecord = result.into(EMPLOYEE_INVITE_STATUS),
            companyTypesRecord = result.into(COMPANY_TYPES),
        )
    }

    override fun getById(id: Int): Result<EmployeeInviteEntity> {
        val result =
            query()
                .where(EMPLOYEE_INVITES.ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Convite não encontrado"))

        return employeeInvitePersistenceMapper.toEntity(
            employeeInvitesRecord = result.into(EMPLOYEE_INVITES),
            roleTypesRecord = result.into(ROLE_TYPES),
            employeeInvitesStatusRecord = result.into(EMPLOYEE_INVITE_STATUS),
            companyTypesRecord = result.into(COMPANY_TYPES),
        )
    }

    override fun getBySub(
        pageable: PageableDTO,
        sub: String,
    ): Result<PageDTO<EmployeeInviteEntity>> =
        runCatching {
            var condition =
                USERS.SUB
                    .eq(sub)
                    .and(EMPLOYEE_INVITES.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        COMPANIES.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(ROLE_TYPES.NAME.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, EMPLOYEE_INVITES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                employeeInvitePersistenceMapper
                    .toEntity(
                        employeeInvitesRecord = it.into(EMPLOYEE_INVITES),
                        roleTypesRecord = it.into(ROLE_TYPES),
                        employeeInvitesStatusRecord = it.into(EMPLOYEE_INVITE_STATUS),
                        companyTypesRecord = it.into(COMPANY_TYPES),
                    ).getOrThrow()
            }
        }

    override fun getByCompanyId(
        companyId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<EmployeeInviteEntity>> =
        runCatching {
            var condition =
                COMPANIES.PUBLIC_ID
                    .eq(companyId)
                    .and(EMPLOYEE_INVITES.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        USERS.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(ROLE_TYPES.NAME.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, EMPLOYEE_INVITES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                employeeInvitePersistenceMapper
                    .toEntity(
                        employeeInvitesRecord = it.into(EMPLOYEE_INVITES),
                        roleTypesRecord = it.into(ROLE_TYPES),
                        employeeInvitesStatusRecord = it.into(EMPLOYEE_INVITE_STATUS),
                        companyTypesRecord = it.into(COMPANY_TYPES),
                    ).getOrThrow()
            }
        }

    override fun save(entity: EmployeeInviteEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = employeeInvitePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(EMPLOYEE_INVITES)
                        .set(record)
                        .where(EMPLOYEE_INVITES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o convite de funcionário")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(EMPLOYEE_INVITES)
                    .set(record)
                    .returning(EMPLOYEE_INVITES.ID, EMPLOYEE_INVITES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o convite de funcionário")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    private fun query() =
        dsl
            .select()
            .from(EMPLOYEE_INVITES)
            .innerJoin(USERS)
            .on(EMPLOYEE_INVITES.USER_ID.eq(USERS.ID))
            .innerJoin(COMPANIES)
            .on(EMPLOYEE_INVITES.COMPANY_ID.eq(COMPANIES.ID))
            .innerJoin(ROLE_TYPES)
            .on(EMPLOYEE_INVITES.ROLE_ID.eq(ROLE_TYPES.ID))
            .innerJoin(COMPANY_TYPES)
            .on(ROLE_TYPES.COMPANY_TYPE_ID.eq(COMPANY_TYPES.ID))
            .innerJoin(EMPLOYEE_INVITE_STATUS)
            .on(EMPLOYEE_INVITES.STATUS_ID.eq(EMPLOYEE_INVITE_STATUS.ID))
}
