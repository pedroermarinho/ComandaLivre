package io.github.pedroermarinho.comandalivreapi.company.core.data.repositories

import company.tables.references.EMPLOYEE_INVITE_STATUS
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.EmployeeInviteStatusRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeInviteStatusPersistenceMapper
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
class JooqEmployeeInviteStatusRepository(
    private val dsl: DSLContext,
    private val employeeInviteStatusPersistenceMapper: EmployeeInviteStatusPersistenceMapper,
) : EmployeeInviteStatusRepository {
    override fun getByKey(key: String): Result<EmployeeInviteStatus> {
        val result =
            query()
                .where(EMPLOYEE_INVITE_STATUS.KEY.eq(key))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de status com o nome $key"))
        return employeeInviteStatusPersistenceMapper.toEntity(result.into(EMPLOYEE_INVITE_STATUS))
    }

    override fun getById(id: Int): Result<EmployeeInviteStatus> {
        val result =
            query()
                .where(EMPLOYEE_INVITE_STATUS.ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de status com o id $id"))
        return employeeInviteStatusPersistenceMapper.toEntity(result.into(EMPLOYEE_INVITE_STATUS))
    }

    override fun getById(id: UUID): Result<EmployeeInviteStatus> {
        val result =
            query()
                .where(EMPLOYEE_INVITE_STATUS.PUBLIC_ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de status com o id $id"))

        return employeeInviteStatusPersistenceMapper.toEntity(result.into(EMPLOYEE_INVITE_STATUS))
    }

    override fun getAll(pageable: PageableDTO): Result<PageDTO<EmployeeInviteStatus>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(EMPLOYEE_INVITE_STATUS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(EMPLOYEE_INVITE_STATUS.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, EMPLOYEE_INVITE_STATUS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) { employeeInviteStatusPersistenceMapper.toEntity(it.into(EMPLOYEE_INVITE_STATUS)).getOrThrow() }
        }

    private fun query() =
        dsl
            .select()
            .from(EMPLOYEE_INVITE_STATUS)

    override fun save(entity: EmployeeInviteStatus): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = employeeInviteStatusPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(EMPLOYEE_INVITE_STATUS)
                        .set(record)
                        .where(EMPLOYEE_INVITE_STATUS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o status do convite de funcionário")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(EMPLOYEE_INVITE_STATUS)
                    .set(record)
                    .returning(EMPLOYEE_INVITE_STATUS.ID, EMPLOYEE_INVITE_STATUS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o status do convite de funcionário")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
