package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ActivityAttachmentEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.ActivityAttachmentForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ActivityAttachmentRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.ActivityAttachmentPersistenceMapper
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
import prumodigital.tables.references.ACTIVITY_ATTACHMENTS
import prumodigital.tables.references.DAILY_ACTIVITIES
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqActivityAttachmentRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val activityAttachmentPersistenceMapper: ActivityAttachmentPersistenceMapper,
) : ActivityAttachmentRepository {
    override fun getByDailyActivityId(
        dailyActivityId: Int,
        pageable: PageableDTO,
    ): Result<PageDTO<ActivityAttachmentEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(DAILY_ACTIVITIES.ID.eq(dailyActivityId))
            condition = condition.and(ACTIVITY_ATTACHMENTS.DELETED_AT.isNull)

            val orderBy = getSortFields(pageable.sort, ACTIVITY_ATTACHMENTS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                activityAttachmentPersistenceMapper.toEntity(it.into(ACTIVITY_ATTACHMENTS)).getOrThrow()
            }
        }

    override fun getById(id: UUID): Result<ActivityAttachmentEntity> {
        val result =
            query()
                .where(ACTIVITY_ATTACHMENTS.PUBLIC_ID.eq(id))
                .and(ACTIVITY_ATTACHMENTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Anexo de atividade não encontrado"))

        return activityAttachmentPersistenceMapper.toEntity(result.into(ACTIVITY_ATTACHMENTS))
    }

    override fun create(form: ActivityAttachmentForm): Result<EntityId> {
        // TODO: implementar isso
        throw BusinessLogicException("não implementado ")
//        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
//        val result =
//            dsl
//                .insertInto(ACTIVITY_ATTACHMENTS)
//                .set(ACTIVITY_ATTACHMENTS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
//                .set(ACTIVITY_ATTACHMENTS.DAILY_ACTIVITY_ID, form.dailyActivityId)
//                .set(ACTIVITY_ATTACHMENTS.ASSET_ID, form.assetId)
//                .set(ACTIVITY_ATTACHMENTS.DESCRIPTION, form.description)
//                .set(ACTIVITY_ATTACHMENTS.CREATED_BY, userAuth.sub)
//                .returning(ACTIVITY_ATTACHMENTS.ID, ACTIVITY_ATTACHMENTS.PUBLIC_ID)
//                .fetchOne()
//                ?: return Result.failure(BusinessLogicException("Não foi possível criar o anexo de atividade"))
//
//        return Result.success(EntityId(result.id!!, result.publicId))
    }

    override fun delete(id: UUID): Result<Unit> {
        val user = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val rowsAffected =
            dsl
                .update(ACTIVITY_ATTACHMENTS)
                .set(ACTIVITY_ATTACHMENTS.UPDATED_BY, user.sub)
                .set(ACTIVITY_ATTACHMENTS.DELETED_AT, LocalDateTime.now())
                .where(ACTIVITY_ATTACHMENTS.PUBLIC_ID.eq(id))
                .and(ACTIVITY_ATTACHMENTS.DELETED_AT.isNull)
                .execute()

        if (rowsAffected == 0) {
            return Result.failure(BusinessLogicException("Não foi possível deletar o anexo de atividade"))
        }
        return Result.success(Unit)
    }

    private fun query() =
        dsl
            .select()
            .from(ACTIVITY_ATTACHMENTS)
            .innerJoin(DAILY_ACTIVITIES)
            .on(ACTIVITY_ATTACHMENTS.DAILY_ACTIVITY_ID.eq(DAILY_ACTIVITIES.ID))

    override fun save(entity: ActivityAttachmentEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = activityAttachmentPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(ACTIVITY_ATTACHMENTS)
                        .set(record)
                        .where(ACTIVITY_ATTACHMENTS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o anexo de atividade")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(ACTIVITY_ATTACHMENTS)
                    .set(record)
                    .returning(ACTIVITY_ATTACHMENTS.ID, ACTIVITY_ATTACHMENTS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o anexo de atividade")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
