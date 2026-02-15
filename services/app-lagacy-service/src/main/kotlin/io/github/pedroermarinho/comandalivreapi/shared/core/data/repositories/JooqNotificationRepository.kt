package io.github.pedroermarinho.comandalivreapi.shared.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.NotificationEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.NotFoundException
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.NotificationRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.NotificationPersistenceMapper
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.toEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.fetchPage
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.util.getSortFields
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import shared.tables.references.NOTIFICATIONS
import java.util.UUID

@Repository
class JooqNotificationRepository(
    private val dsl: DSLContext,
    private val notificationPersistenceMapper: NotificationPersistenceMapper,
) : NotificationRepository {
    override fun getAll(pageable: PageableDTO): Result<PageDTO<NotificationEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(NOTIFICATIONS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        NOTIFICATIONS.TITLE
                            .likeIgnoreCase("%${pageable.search}%"),
                    )
            }

            val orderBy = getSortFields(pageable.sort, NOTIFICATIONS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                notificationPersistenceMapper.toEntity(it.into(NOTIFICATIONS)).getOrThrow()
            }
        }

    private fun query() =
        dsl
            .select()
            .from(NOTIFICATIONS)

    override fun getByUserId(
        userId: Int,
        pageable: PageableDTO,
    ): Result<PageDTO<NotificationEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(NOTIFICATIONS.USER_ID.eq(userId))
            condition = condition.and(NOTIFICATIONS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        NOTIFICATIONS.TITLE
                            .likeIgnoreCase("%${pageable.search}%")
                            .or(NOTIFICATIONS.MESSAGE.likeIgnoreCase("%${pageable.search}%")),
                    )
            }

            val orderBy = getSortFields(pageable.sort, NOTIFICATIONS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                notificationPersistenceMapper.toEntity(it.into(NOTIFICATIONS)).getOrThrow()
            }
        }

    override fun countUnreadByUserId(userId: Int): Result<Long> =
        runCatching {
            dsl
                .selectCount()
                .from(NOTIFICATIONS)
                .where(NOTIFICATIONS.USER_ID.eq(userId))
                .and(NOTIFICATIONS.STATUS.isFalse)
                .and(NOTIFICATIONS.DELETED_AT.isNull)
                .fetchOne(0, Long::class.java) ?: 0L
        }

    override fun save(entity: NotificationEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.isNew()
            val record = notificationPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(NOTIFICATIONS)
                        .set(record)
                        .where(NOTIFICATIONS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a notificação")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }

            record.changed(NOTIFICATIONS.ID, false)
            val result =
                dsl
                    .insertInto(NOTIFICATIONS)
                    .set(record)
                    .returning(NOTIFICATIONS.ID, NOTIFICATIONS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a notificação")

            return@runCatching EntityId(result.id!!, result.publicId)
        }

    override fun getById(id: UUID): Result<NotificationEntity> =
        runCatching {
            val record =
                dsl
                    .select()
                    .from(NOTIFICATIONS)
                    .where(NOTIFICATIONS.PUBLIC_ID.eq(id))
                    .and(NOTIFICATIONS.DELETED_AT.isNull)
                    .fetchOne() ?: throw NotFoundException("Notificação não encontrada para o ID: $id")

            notificationPersistenceMapper.toEntity(record.into(NOTIFICATIONS)).getOrThrow()
        }
}
