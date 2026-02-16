package io.github.pedroermarinho.company.repositories

import company.tables.references.COMPANY_TYPES
import company.tables.references.ROLE_TYPES
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.RoleTypeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.RoleTypePersistenceMapper
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
class JooqRoleTypeRepository(
    private val dsl: DSLContext,
    private val roleTypePersistenceMapper: RoleTypePersistenceMapper,
) : RoleTypeRepository {
    override fun getByName(value: String): Result<RoleType> {
        val result =
            query()
                .where(ROLE_TYPES.NAME.eq(value))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de papel com o nome $value"))

        return roleTypePersistenceMapper.toEntity(roleTypesRecord = result.into(ROLE_TYPES), companyTypesRecord = result.into(COMPANY_TYPES))
    }

    override fun getByKey(key: String): Result<RoleType> {
        val result =
            query()
                .where(ROLE_TYPES.KEY.eq(key))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de papel com o nome $key"))

        return roleTypePersistenceMapper.toEntity(roleTypesRecord = result.into(ROLE_TYPES), companyTypesRecord = result.into(COMPANY_TYPES))
    }

    override fun getById(id: Int): Result<RoleType> {
        val result =
            query()
                .where(ROLE_TYPES.ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de papel com o id $id"))

        return roleTypePersistenceMapper.toEntity(roleTypesRecord = result.into(ROLE_TYPES), companyTypesRecord = result.into(COMPANY_TYPES))
    }

    override fun getById(id: UUID): Result<RoleType> {
        val result =
            query()
                .where(ROLE_TYPES.PUBLIC_ID.eq(id))
                .fetchOne()
                ?: return Result.failure(NotFoundException("Não foi possível encontrar o tipo de papel com o id $id"))

        return roleTypePersistenceMapper.toEntity(roleTypesRecord = result.into(ROLE_TYPES), companyTypesRecord = result.into(COMPANY_TYPES))
    }

    override fun getAll(pageable: PageableDTO): Result<PageDTO<RoleType>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(ROLE_TYPES.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(ROLE_TYPES.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, ROLE_TYPES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) { roleTypePersistenceMapper.toEntity(roleTypesRecord = it.into(ROLE_TYPES), companyTypesRecord = it.into(COMPANY_TYPES)).getOrThrow() }
        }

    override fun getAll(): Result<List<RoleType>> =
        runCatching {
            query()
                .where(ROLE_TYPES.DELETED_AT.isNull)
                .orderBy(ROLE_TYPES.NAME.asc())
                .fetch()
                .map { roleTypePersistenceMapper.toEntity(roleTypesRecord = it.into(ROLE_TYPES), companyTypesRecord = it.into(COMPANY_TYPES)).getOrThrow() }
        }

    private fun query() =
        dsl
            .select()
            .from(ROLE_TYPES)
            .innerJoin(COMPANY_TYPES)
            .on(ROLE_TYPES.COMPANY_TYPE_ID.eq(COMPANY_TYPES.ID))

    override fun save(entity: RoleType): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = roleTypePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(ROLE_TYPES)
                        .set(record)
                        .where(ROLE_TYPES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o tipo de papel")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(ROLE_TYPES)
                    .set(record)
                    .returning(ROLE_TYPES.ID, ROLE_TYPES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o tipo de papel")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
