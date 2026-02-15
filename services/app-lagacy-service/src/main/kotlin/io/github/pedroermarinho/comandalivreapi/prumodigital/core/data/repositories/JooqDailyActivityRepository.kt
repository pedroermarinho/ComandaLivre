package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyActivityEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyActivityForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyActivityPersistenceMapper
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
import prumodigital.tables.references.DAILY_ACTIVITIES
import prumodigital.tables.references.DAILY_REPORTS
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqDailyActivityRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val dailyActivityPersistenceMapper: DailyActivityPersistenceMapper,
) : DailyActivityRepository {
    override fun getByDailyReportId(
        dailyReportId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<DailyActivityEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(DAILY_REPORTS.PUBLIC_ID.eq(dailyReportId))
            condition = condition.and(DAILY_ACTIVITIES.DELETED_AT.isNull)

            val orderBy = getSortFields(pageable.sort, DAILY_ACTIVITIES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                dailyActivityPersistenceMapper.toEntity(it.into(DAILY_ACTIVITIES)).getOrThrow()
            }
        }

    override fun getById(id: UUID): Result<DailyActivityEntity> {
        val result =
            query()
                .where(DAILY_ACTIVITIES.PUBLIC_ID.eq(id))
                .and(DAILY_ACTIVITIES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Atividade diária não encontrada"))

        return dailyActivityPersistenceMapper.toEntity(result.into(DAILY_ACTIVITIES))
    }

    override fun getById(id: Int): Result<DailyActivityEntity> {
        val result =
            query()
                .where(DAILY_ACTIVITIES.ID.eq(id))
                .and(DAILY_ACTIVITIES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Atividade diária não encontrada"))

        return dailyActivityPersistenceMapper.toEntity(result.into(DAILY_ACTIVITIES))
    }

    override fun create(form: DailyActivityForm): Result<EntityId> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .insertInto(DAILY_ACTIVITIES)
                .set(DAILY_ACTIVITIES.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                .set(DAILY_ACTIVITIES.DAILY_REPORT_ID, form.dailyReportId)
                .set(DAILY_ACTIVITIES.ACTIVITY_DESCRIPTION, form.activityDescription)
                .set(DAILY_ACTIVITIES.STATUS_ID, form.statusId)
                .set(DAILY_ACTIVITIES.LOCATION_DESCRIPTION, form.locationDescription)
                .set(DAILY_ACTIVITIES.RESPONSIBLE_EMPLOYEE_ASSIGNMENT_ID, form.responsibleEmployeeAssignmentId)
                .set(DAILY_ACTIVITIES.CREATED_BY, userAuth.sub)
                .returning(DAILY_ACTIVITIES.ID, DAILY_ACTIVITIES.PUBLIC_ID)
                .fetchOne()
                ?: return Result.failure(BusinessLogicException("Não foi possível criar a atividade diária"))

        return Result.success(EntityId(result.id!!, result.publicId))
    }

    override fun update(
        id: UUID,
        form: DailyActivityForm,
    ): Result<Unit> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .update(DAILY_ACTIVITIES)
                .set(DAILY_ACTIVITIES.DAILY_REPORT_ID, form.dailyReportId)
                .set(DAILY_ACTIVITIES.ACTIVITY_DESCRIPTION, form.activityDescription)
                .set(DAILY_ACTIVITIES.STATUS_ID, form.statusId)
                .set(DAILY_ACTIVITIES.LOCATION_DESCRIPTION, form.locationDescription)
                .set(DAILY_ACTIVITIES.RESPONSIBLE_EMPLOYEE_ASSIGNMENT_ID, form.responsibleEmployeeAssignmentId)
                .set(DAILY_ACTIVITIES.UPDATED_BY, userAuth.sub)
                .where(DAILY_ACTIVITIES.PUBLIC_ID.eq(id))
                .and(DAILY_ACTIVITIES.DELETED_AT.isNull)
                .execute()

        if (result <= 0) {
            return Result.failure(BusinessLogicException("Não foi possível atualizar a atividade diária"))
        }

        return Result.success(Unit)
    }

    override fun delete(id: UUID): Result<Unit> {
        val user = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val rowsAffected =
            dsl
                .update(DAILY_ACTIVITIES)
                .set(DAILY_ACTIVITIES.UPDATED_BY, user.sub)
                .set(DAILY_ACTIVITIES.DELETED_AT, LocalDateTime.now())
                .where(DAILY_ACTIVITIES.PUBLIC_ID.eq(id))
                .and(DAILY_ACTIVITIES.DELETED_AT.isNull)
                .execute()

        if (rowsAffected == 0) {
            return Result.failure(BusinessLogicException("Não foi possível deletar a atividade diária"))
        }
        return Result.success(Unit)
    }

    private fun query() =
        dsl
            .select()
            .from(DAILY_ACTIVITIES)
            .innerJoin(DAILY_REPORTS)
            .on(DAILY_ACTIVITIES.DAILY_REPORT_ID.eq(DAILY_REPORTS.ID))

    override fun save(entity: DailyActivityEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = dailyActivityPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(DAILY_ACTIVITIES)
                        .set(record)
                        .where(DAILY_ACTIVITIES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a atividade diária")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(DAILY_ACTIVITIES)
                    .set(record)
                    .returning(DAILY_ACTIVITIES.ID, DAILY_ACTIVITIES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a atividade diária")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
