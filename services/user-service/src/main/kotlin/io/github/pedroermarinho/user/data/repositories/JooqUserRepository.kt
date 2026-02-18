package io.github.pedroermarinho.user.data.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserFilterDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserRegistrationsPerDayDTO
import io.github.pedroermarinho.user.domain.entities.UserEntity
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.user.domain.repositories.UserRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.user.infra.mappers.UserPersistenceMapper
import io.github.pedroermarinho.shared.util.fetchPage
import io.github.pedroermarinho.shared.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import user.tables.references.FEATURE_GROUPS
import user.tables.references.USERS
import user.tables.references.USER_FEATURE_GROUPS
import java.time.LocalDate
import java.util.*

@Repository
class JooqUserRepository(
    private val dsl: DSLContext,
    private val userPersistenceMapper: UserPersistenceMapper,
) : UserRepository {
    override fun getAll(
        pageable: PageableDTO,
        filter: UserFilterDTO,
    ): Result<PageDTO<UserEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()

            if (pageable.search != null) {
                condition =
                    condition.and(
                        USERS.NAME
                            .likeIgnoreCase("${pageable.search}%")
                            .or(USERS.EMAIL.likeIgnoreCase("${pageable.search}%")),
                    )
            }

            if (filter.group != null) {
                condition =
                    condition.and(
                        DSL.exists(
                            dsl
                                .selectOne()
                                .from(USER_FEATURE_GROUPS)
                                .join(FEATURE_GROUPS)
                                .on(FEATURE_GROUPS.ID.eq(USER_FEATURE_GROUPS.FEATURE_GROUP_ID))
                                .where(
                                    USER_FEATURE_GROUPS.USER_ID
                                        .eq(USERS.ID)
                                        .and(FEATURE_GROUPS.PUBLIC_ID.eq(filter.group))
                                        .and(USER_FEATURE_GROUPS.IS_ACTIVE.eq(true)),
                                ),
                        ),
                    )
            }

            if (filter.excludeGroup != null) {
                condition =
                    condition.and(
                        DSL.notExists(
                            dsl
                                .selectOne()
                                .from(USER_FEATURE_GROUPS)
                                .join(FEATURE_GROUPS)
                                .on(FEATURE_GROUPS.ID.eq(USER_FEATURE_GROUPS.FEATURE_GROUP_ID))
                                .where(
                                    USER_FEATURE_GROUPS.USER_ID
                                        .eq(USERS.ID)
                                        .and(FEATURE_GROUPS.PUBLIC_ID.eq(filter.excludeGroup))
                                        .and(USER_FEATURE_GROUPS.IS_ACTIVE.eq(true)),
                                ),
                        ),
                    )
            }

            val orderBy = getSortFields(pageable.sort, USERS).getOrNull()

            val query: SelectJoinStep<Record> = dsl.select().from(USERS)

            fetchPage(
                dsl = dsl,
                baseQuery = query,
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                userPersistenceMapper.toEntity(it.into(USERS)).getOrThrow()
            }
        }

    override fun getBySub(sub: String): Result<UserEntity> {
        val result =
            dsl
                .select()
                .from(USERS)
                .where(USERS.SUB.eq(sub))
                .and(USERS.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Usuário não encontrado"))
        return userPersistenceMapper.toEntity(result.into(USERS))
    }

    override fun getByEmail(email: String): Result<UserEntity> {
        val result =
            dsl
                .select()
                .from(USERS)
                .where(USERS.EMAIL.equalIgnoreCase(email))
                .and(USERS.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Usuário não encontrado"))
        return userPersistenceMapper.toEntity(result.into(USERS))
    }

    override fun existsBySub(sub: String): Boolean =
        dsl.fetchExists(
            dsl
                .select(USERS.ID)
                .from(USERS)
                .where(USERS.SUB.eq(sub))
                .and(USERS.DELETED_AT.isNull),
        )

    override fun getById(id: Int): Result<UserEntity> {
        val result =
            dsl
                .select()
                .from(USERS)
                .where(USERS.ID.eq(id))
                .and(USERS.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Usuário não encontrado"))
        return userPersistenceMapper.toEntity(result.into(USERS))
    }

    override fun getById(id: UUID): Result<UserEntity> {
        val result =
            dsl
                .select()
                .from(USERS)
                .where(USERS.PUBLIC_ID.eq(id))
                .and(USERS.DELETED_AT.isNull)
                .fetchOne() ?: return Result.failure(NotFoundException("Usuário não encontrado"))
        return userPersistenceMapper.toEntity(result.into(USERS))
    }

    override fun getIdBySub(sub: String): Result<Int> =
        dsl
            .select(USERS.ID)
            .from(USERS)
            .where(USERS.SUB.eq(sub))
            .and(USERS.DELETED_AT.isNull)
            .fetchOne()
            ?.get(USERS.ID)
            ?.let { Result.success(it) }
            ?: Result.failure(NotFoundException("Usuário não encontrado"))

    override fun count(): Result<Long> =
        runCatching {
            dsl.fetchCount(USERS.where(USERS.DELETED_AT.isNull)).toLong()
        }

    override fun getUserRegistrationsLastDays(days: Long): Result<List<UserRegistrationsPerDayDTO>> =
        runCatching {
            val registrationDateField = DSL.field("DATE({0})", LocalDate::class.java, USERS.CREATED_AT)

            dsl
                .select(
                    registrationDateField.`as`("registration_date"),
                    DSL.count().`as`("user_count"),
                ).from(USERS)
                .where(USERS.CREATED_AT.greaterOrEqual(LocalDate.now().minusDays(days).atStartOfDay()))
                .and(USERS.DELETED_AT.isNull)
                .groupBy(registrationDateField)
                .orderBy(registrationDateField)
                .fetch()
                .map {
                    UserRegistrationsPerDayDTO(
                        date = it.get("registration_date", LocalDate::class.java),
                        userCount = it.get("user_count", Int::class.java),
                    )
                }
        }

    override fun getIdByPublicId(id: UUID): Result<EntityId> {
        val result =
            dsl
                .select(USERS.ID, USERS.PUBLIC_ID)
                .from(USERS)
                .where(USERS.PUBLIC_ID.eq(id))
                .fetchOne() ?: return Result.failure(NotFoundException("Usuário não encontrado"))

        return Result.success(EntityId(result[USERS.ID]!!, result[USERS.PUBLIC_ID]!!))
    }

    override fun save(entity: UserEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = userPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(USERS)
                        .set(record)
                        .where(USERS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o usuário")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(USERS)
                    .set(record)
                    .returning(USERS.ID, USERS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o usuário")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
