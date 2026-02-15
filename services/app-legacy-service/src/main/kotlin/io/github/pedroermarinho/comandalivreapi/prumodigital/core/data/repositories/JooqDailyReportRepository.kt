package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyReportEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyReportForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyReportRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyReportPersistenceMapper
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
import prumodigital.tables.references.DAILY_REPORTS
import prumodigital.tables.references.PROJECTS
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqDailyReportRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val dailyReportPersistenceMapper: DailyReportPersistenceMapper,
) : DailyReportRepository {
    override fun getAll(
        projectId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<DailyReportEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(PROJECTS.PUBLIC_ID.eq(projectId))
            condition = condition.and(DAILY_REPORTS.DELETED_AT.isNull)

            if (pageable.search != null) {
                condition =
                    condition.and(
                        DAILY_REPORTS.GENERAL_OBSERVATIONS
                            .likeIgnoreCase("%${pageable.search}%"),
                    )
            }

            val orderBy = getSortFields(pageable.sort, DAILY_REPORTS).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                dailyReportPersistenceMapper.toEntity(it.into(DAILY_REPORTS)).getOrThrow()
            }
        }

    override fun getById(id: UUID): Result<DailyReportEntity> {
        val result =
            query()
                .where(DAILY_REPORTS.PUBLIC_ID.eq(id))
                .and(DAILY_REPORTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Relatório diário não encontrado"))
        return dailyReportPersistenceMapper.toEntity(result.into(DAILY_REPORTS))
    }

    override fun getById(id: Int): Result<DailyReportEntity> {
        val result =
            query()
                .where(DAILY_REPORTS.ID.eq(id))
                .and(DAILY_REPORTS.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Relatório diário não encontrado"))

        return dailyReportPersistenceMapper.toEntity(result.into(DAILY_REPORTS))
    }

    override fun create(form: DailyReportForm): Result<EntityId> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .insertInto(DAILY_REPORTS)
                .set(DAILY_REPORTS.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                .set(DAILY_REPORTS.PROJECT_ID, form.projectId)
                .set(DAILY_REPORTS.REPORT_DATE, form.reportDate)
                .set(DAILY_REPORTS.GENERAL_OBSERVATIONS, form.generalObservations)
                .set(DAILY_REPORTS.MORNING_WEATHER_ID, form.morningWeatherId)
                .set(DAILY_REPORTS.AFTERNOON_WEATHER_ID, form.afternoonWeatherId)
                .set(DAILY_REPORTS.WORK_START_TIME, form.workStartTime)
                .set(DAILY_REPORTS.LUNCH_START_TIME, form.lunchStartTime)
                .set(DAILY_REPORTS.LUNCH_END_TIME, form.lunchEndTime)
                .set(DAILY_REPORTS.WORK_END_TIME, form.workEndTime)
                .set(DAILY_REPORTS.REPORTED_BY_ASSIGNMENT_ID, form.reportedByAssignmentId)
                .set(DAILY_REPORTS.CREATED_BY, userAuth.sub)
                .returning(DAILY_REPORTS.ID, DAILY_REPORTS.PUBLIC_ID)
                .fetchOne()
                ?: return Result.failure(BusinessLogicException("Não foi possível criar o relatório diário"))

        return Result.success(EntityId(result.id!!, result.publicId))
    }

    override fun update(
        id: UUID,
        form: DailyReportForm,
    ): Result<Unit> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .update(DAILY_REPORTS)
                .set(DAILY_REPORTS.PROJECT_ID, form.projectId)
                .set(DAILY_REPORTS.REPORT_DATE, form.reportDate)
                .set(DAILY_REPORTS.GENERAL_OBSERVATIONS, form.generalObservations)
                .set(DAILY_REPORTS.MORNING_WEATHER_ID, form.morningWeatherId)
                .set(DAILY_REPORTS.AFTERNOON_WEATHER_ID, form.afternoonWeatherId)
                .set(DAILY_REPORTS.WORK_START_TIME, form.workStartTime)
                .set(DAILY_REPORTS.LUNCH_START_TIME, form.lunchStartTime)
                .set(DAILY_REPORTS.LUNCH_END_TIME, form.lunchEndTime)
                .set(DAILY_REPORTS.WORK_END_TIME, form.workEndTime)
                .set(DAILY_REPORTS.REPORTED_BY_ASSIGNMENT_ID, form.reportedByAssignmentId)
                .set(DAILY_REPORTS.UPDATED_BY, userAuth.sub)
                .where(DAILY_REPORTS.PUBLIC_ID.eq(id))
                .and(DAILY_REPORTS.DELETED_AT.isNull)
                .execute()

        if (result == 0) {
            return Result.failure(BusinessLogicException("Não foi possível atualizar o relatório diário"))
        }
        return Result.success(Unit)
    }

    override fun delete(id: UUID): Result<Unit> {
        val user = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val rowsAffected =
            dsl
                .update(DAILY_REPORTS)
                .set(DAILY_REPORTS.UPDATED_BY, user.sub)
                .set(DAILY_REPORTS.DELETED_AT, LocalDateTime.now())
                .where(DAILY_REPORTS.PUBLIC_ID.eq(id))
                .and(DAILY_REPORTS.DELETED_AT.isNull)
                .execute()

        if (rowsAffected == 0) {
            return Result.failure(BusinessLogicException("Não foi possível deletar o relatório diário"))
        }
        return Result.success(Unit)
    }

    override fun existsByProjectIdAndReportDate(
        projectId: Int,
        reportDate: LocalDate,
    ): Result<Boolean> =
        runCatching {
            val result =
                dsl.fetchExists(
                    dsl
                        .select()
                        .from(DAILY_REPORTS)
                        .where(DAILY_REPORTS.PROJECT_ID.eq(projectId))
                        .and(DAILY_REPORTS.REPORT_DATE.eq(reportDate))
                        .and(DAILY_REPORTS.DELETED_AT.isNull),
                )

            return Result.success(result)
        }

    private fun query() =
        dsl
            .select()
            .from(DAILY_REPORTS)
            .innerJoin(PROJECTS)
            .on(DAILY_REPORTS.PROJECT_ID.eq(PROJECTS.ID))

    override fun save(entity: DailyReportEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = dailyReportPersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(DAILY_REPORTS)
                        .set(record)
                        .where(DAILY_REPORTS.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar o relatório diário")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(DAILY_REPORTS)
                    .set(record)
                    .returning(DAILY_REPORTS.ID, DAILY_REPORTS.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar o relatório diário")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
