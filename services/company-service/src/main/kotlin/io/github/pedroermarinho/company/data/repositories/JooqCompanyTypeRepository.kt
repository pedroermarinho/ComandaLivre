package io.github.pedroermarinho.company.repositories

import company.tables.references.COMPANY_TYPES
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyTypeRepository
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyTypePersistenceMapper
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
class JooqCompanyTypeRepository(
    private val dsl: DSLContext,
    private val companyTypePersistenceMapper: CompanyTypePersistenceMapper,
) : CompanyTypeRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<CompanyType>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(COMPANY_TYPES.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition = condition.and(COMPANY_TYPES.NAME.likeIgnoreCase("%${pageable.search}%"))
            }

            val orderBy = getSortFields(pageable.sort, COMPANY_TYPES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) { companyTypePersistenceMapper.toEntity(it.into(COMPANY_TYPES)).getOrThrow() }
        }

    override fun getAll(): Result<List<CompanyType>> =
        runCatching {
            query()
                .where(COMPANY_TYPES.DELETED_AT.isNull)
                .orderBy(COMPANY_TYPES.NAME.asc())
                .fetch()
                .map { companyTypePersistenceMapper.toEntity(it.into(COMPANY_TYPES)).getOrThrow() }
        }

    override fun getById(id: Int): Result<CompanyType> {
        val result =
            dsl
                .select()
                .from(COMPANY_TYPES)
                .where(COMPANY_TYPES.ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Restaurante não encontrado"))
        return companyTypePersistenceMapper.toEntity(result.into(COMPANY_TYPES))
    }

    override fun getByKey(key: String): Result<CompanyType> {
        val result =
            dsl
                .select()
                .from(COMPANY_TYPES)
                .where(COMPANY_TYPES.KEY.eq(key))
                .fetchOne() ?: return Result.failure(NotFoundException("Tipo de empresa não encontrado"))
        return companyTypePersistenceMapper.toEntity(result.into(COMPANY_TYPES))
    }

    override fun getById(id: UUID): Result<CompanyType> {
        val result =
            dsl
                .select()
                .from(COMPANY_TYPES)
                .where(COMPANY_TYPES.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Tipo de empresa não encontrado com o ID público fornecido"))
        return companyTypePersistenceMapper.toEntity(result.into(COMPANY_TYPES))
    }

    private fun query() =
        dsl
            .select()
            .from(COMPANY_TYPES)

    override fun save(entity: CompanyType): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = companyTypePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(COMPANY_TYPES)
                        .set(record)
                        .where(COMPANY_TYPES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o tipo de empresa")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(COMPANY_TYPES)
                    .set(record)
                    .returning(COMPANY_TYPES.ID, COMPANY_TYPES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o tipo de empresa")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
