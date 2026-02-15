package io.github.pedroermarinho.comandalivreapi.company.core.data.repositories

import company.tables.references.COMPANIES
import company.tables.references.COMPANY_SETTINGS
import company.tables.references.COMPANY_TYPES
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyCountByTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanyEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.entities.CompanySettingsEntity
import io.github.pedroermarinho.comandalivreapi.company.core.domain.repositories.CompanyRepository
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanyPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.CompanySettingsPersistenceMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.services.CurrentUserService
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JooqCompanyRepository(
    private val dsl: DSLContext,
    private val companyPersistenceMapper: CompanyPersistenceMapper,
    private val companySettingsPersistenceMapper: CompanySettingsPersistenceMapper,
    private val currentUserService: CurrentUserService,
) : CompanyRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<CompanyEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(COMPANIES.DELETED_AT.isNull)
            condition = condition.and(COMPANIES.IS_PUBLIC.isTrue)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        COMPANIES.NAME
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(COMPANIES.DESCRIPTION.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, COMPANIES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                companyPersistenceMapper
                    .toEntity(
                        company = it.into(COMPANIES),
                        settings = it.into(COMPANY_SETTINGS),
                        companyType = it.into(COMPANY_TYPES),
                    ).getOrThrow()
            }
        }

    override fun getById(id: Int): Result<CompanyEntity> =
        runCatching {
            val result =
                query()
                    .where(COMPANIES.ID.eq(id))
                    .fetchOne() ?: return Result.failure(NotFoundException("Restaurante não encontrado"))
            companyPersistenceMapper
                .toEntity(
                    company = result.into(COMPANIES),
                    settings = result.into(COMPANY_SETTINGS),
                    companyType = result.into(COMPANY_TYPES),
                ).getOrThrow()
        }

    override fun save(company: CompanyEntity): Result<EntityId> =
        runCatching {
            val isNew = company.id.isNew()
            val companyRecord = companyPersistenceMapper.toRecord(company).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(COMPANIES)
                        .set(companyRecord)
                        .where(COMPANIES.ID.eq(company.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a empresa")
                }
                saveSettings(
                    company.settings.updateCompany(
                        companyId = company.id.internalId,
                    ),
                ).getOrThrow()

                return@runCatching EntityId(company.id.internalId, company.id.publicId)
            }
            val result =
                dsl
                    .insertInto(COMPANIES)
                    .set(companyRecord)
                    .returning(COMPANIES.ID, COMPANIES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a empresa")

            saveSettings(
                company.settings.updateCompany(
                    companyId = result.id!!,
                ),
            ).getOrThrow()

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    override fun getById(id: UUID): Result<CompanyEntity> {
        val result =
            query()
                .where(COMPANIES.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Restaurante não encontrado"))
        return companyPersistenceMapper.toEntity(
            company = result.into(COMPANIES),
            settings = result.into(COMPANY_SETTINGS),
            companyType = result.into(COMPANY_TYPES),
        )
    }

    override fun getPrivateIdByPublicId(companyPublicId: UUID): Result<Int> {
        val result =
            dsl
                .select(COMPANIES.ID)
                .from(COMPANIES)
                .where(COMPANIES.PUBLIC_ID.eq(companyPublicId))
                .fetchOne()
                ?.get(COMPANIES.ID) ?: return Result.failure(NotFoundException("Restaurante não encontrado"))
        return Result.success(result)
    }

    override fun count(): Result<Long> =
        runCatching {
            dsl.fetchCount(COMPANIES.where(COMPANIES.DELETED_AT.isNull)).toLong()
        }

    override fun countByType(): Result<List<CompanyCountByTypeDTO>> =
        runCatching {
            dsl
                .select(
                    COMPANY_TYPES.NAME.`as`("type_name"),
                    DSL.count(COMPANIES.ID).`as`("company_count"),
                ).from(COMPANY_TYPES)
                .leftJoin(COMPANIES)
                .on(COMPANIES.COMPANY_TYPE_ID.eq(COMPANY_TYPES.ID))
                .where(COMPANIES.DELETED_AT.isNull)
                .groupBy(COMPANY_TYPES.ID, COMPANY_TYPES.NAME)
                .orderBy(COMPANY_TYPES.NAME)
                .fetch()
                .map {
                    CompanyCountByTypeDTO(
                        typeName = it.get("type_name", String::class.java),
                        companyCount = it.get("company_count", Long::class.java),
                    )
                }
        }

    override fun getSettingsIdByPublicId(companyPublicId: UUID): Result<Int> {
        val result =
            dsl
                .select(COMPANY_SETTINGS.ID)
                .from(COMPANY_SETTINGS)
                .innerJoin(COMPANIES)
                .on(COMPANIES.ID.eq(COMPANY_SETTINGS.COMPANY_ID))
                .where(COMPANIES.PUBLIC_ID.eq(companyPublicId))
                .fetchOne()
                ?.get(COMPANY_SETTINGS.ID)
                ?: return Result.failure(NotFoundException("Restaurante não encontrado"))
        return Result.success(result)
    }

    override fun getAddressIdByCompanyId(companyId: UUID): Result<Int> {
        val result =
            dsl
                .select(COMPANIES.ADDRESS_ID)
                .from(COMPANIES)
                .where(COMPANIES.PUBLIC_ID.eq(companyId))
                .fetchOne()
                ?.get(COMPANIES.ADDRESS_ID) ?: return Result.failure(NotFoundException("Endereço não encontrado"))
        return Result.success(result)
    }

    override fun getByDomain(domain: String): Result<CompanyEntity> {
        val result =
            query()
                .where(COMPANY_SETTINGS.DOMAIN.eq(domain))
                .and(COMPANIES.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Restaurante não encontrado"))
        return companyPersistenceMapper.toEntity(
            company = result.into(COMPANIES),
            settings = result.into(COMPANY_SETTINGS),
            companyType = result.into(COMPANY_TYPES),
        )
    }

    override fun existDomain(domain: String): Boolean =
        dsl.fetchExists(
            dsl
                .selectOne()
                .from(COMPANY_SETTINGS)
                .where(COMPANY_SETTINGS.DOMAIN.eq(domain)),
        )

    override fun existsByName(name: String): Boolean =
        dsl.fetchExists(
            dsl
                .selectOne()
                .from(COMPANIES)
                .where(COMPANIES.NAME.eq(name)),
        )

    override fun exists(id: UUID): Boolean =
        dsl.fetchExists(
            dsl
                .selectOne()
                .from(COMPANIES)
                .where(COMPANIES.PUBLIC_ID.eq(id))
                .and(COMPANIES.DELETED_AT.isNull),
        )

    private fun query() =
        dsl
            .select()
            .from(COMPANIES)
            .innerJoin(COMPANY_TYPES)
            .on(COMPANIES.COMPANY_TYPE_ID.eq(COMPANY_TYPES.ID))
            .leftJoin(COMPANY_SETTINGS)
            .on(COMPANIES.ID.eq(COMPANY_SETTINGS.COMPANY_ID))

    private fun saveSettings(entity: CompanySettingsEntity): Result<EntityId> =
        runCatching {
            if (entity.companyId.isInvalid()) {
                throw BusinessLogicException("Empresa inválida para salvar as configurações")
            }

            val isNew = entity.id.isNew()
            val record = companySettingsPersistenceMapper.toRecord(entity).getOrThrow()

            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(COMPANY_SETTINGS)
                        .set(record)
                        .where(COMPANY_SETTINGS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar as configurações da empresa")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }

            val result =
                dsl
                    .insertInto(COMPANY_SETTINGS)
                    .set(record)
                    .returning(COMPANY_SETTINGS.ID, COMPANY_SETTINGS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar as configurações da empresa")

            EntityId(result.id!!, result.publicId)
        }
}
