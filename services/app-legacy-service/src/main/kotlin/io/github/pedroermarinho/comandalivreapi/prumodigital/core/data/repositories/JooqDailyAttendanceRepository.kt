package io.github.pedroermarinho.comandalivreapi.prumodigital.core.data.repositories

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyAttendanceEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyAttendanceForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyAttendanceRepository
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.DailyAttendancePersistenceMapper
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
import prumodigital.tables.references.DAILY_ATTENDANCES
import prumodigital.tables.references.DAILY_REPORTS
import java.time.LocalDateTime
import java.util.*

@Repository
class JooqDailyAttendanceRepository(
    private val dsl: DSLContext,
    private val currentUserService: CurrentUserService,
    private val dailyAttendancePersistenceMapper: DailyAttendancePersistenceMapper,
) : DailyAttendanceRepository {
    override fun getAll(
        pageable: PageableDTO,
        dailyReportId: Int,
    ): Result<PageDTO<DailyAttendanceEntity>> =
        runCatching {
            var condition: Condition = DSL.trueCondition()
            condition = condition.and(DAILY_REPORTS.ID.eq(dailyReportId))
            condition = condition.and(DAILY_ATTENDANCES.DELETED_AT.isNull)

            val orderBy = getSortFields(pageable.sort, DAILY_ATTENDANCES).getOrNull()

            fetchPage(
                dsl = dsl,
                baseQuery = query(),
                condition = condition,
                pageable = pageable,
                orderBy = orderBy,
            ) {
                dailyAttendancePersistenceMapper.toEntity(it.into(DAILY_ATTENDANCES)).getOrThrow()
            }
        }

    override fun getById(id: UUID): Result<DailyAttendanceEntity> {
        val result =
            query()
                .where(DAILY_ATTENDANCES.PUBLIC_ID.eq(id))
                .and(DAILY_ATTENDANCES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Presença diária não encontrada"))

        return dailyAttendancePersistenceMapper.toEntity(result.into(DAILY_ATTENDANCES))
    }

    override fun getById(id: Int): Result<DailyAttendanceEntity> {
        val result =
            query()
                .where(DAILY_ATTENDANCES.ID.eq(id))
                .and(DAILY_ATTENDANCES.DELETED_AT.isNull)
                .fetchOne()
                ?: return Result.failure(NotFoundException("Presença diária não encontrada"))

        return dailyAttendancePersistenceMapper.toEntity(result.into(DAILY_ATTENDANCES))
    }

    override fun create(form: DailyAttendanceForm): Result<EntityId> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .insertInto(DAILY_ATTENDANCES)
                .set(DAILY_ATTENDANCES.PUBLIC_ID, UuidCreator.getTimeOrderedEpoch())
                .set(DAILY_ATTENDANCES.DAILY_REPORT_ID, form.dailyReportId)
                .set(DAILY_ATTENDANCES.EMPLOYEE_ASSIGNMENT_ID, form.employeeAssignmentId)
                .set(DAILY_ATTENDANCES.PRESENT, form.present)
                .set(DAILY_ATTENDANCES.ARRIVAL_TIME, form.arrivalTime)
                .set(DAILY_ATTENDANCES.DEPARTURE_TIME, form.departureTime)
                .set(DAILY_ATTENDANCES.ATTENDANCE_NOTE, form.attendanceNote)
                .set(DAILY_ATTENDANCES.CREATED_BY, userAuth.sub)
                .returning(DAILY_ATTENDANCES.ID, DAILY_ATTENDANCES.PUBLIC_ID)
                .fetchOne()
                ?: return Result.failure(BusinessLogicException("Não foi possível criar a presença diária"))

        return Result.success(EntityId(result.id!!, result.publicId))
    }

    override fun update(
        id: UUID,
        form: DailyAttendanceForm,
    ): Result<Unit> {
        val userAuth = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val result =
            dsl
                .update(DAILY_ATTENDANCES)
                .set(DAILY_ATTENDANCES.DAILY_REPORT_ID, form.dailyReportId)
                .set(DAILY_ATTENDANCES.EMPLOYEE_ASSIGNMENT_ID, form.employeeAssignmentId)
                .set(DAILY_ATTENDANCES.PRESENT, form.present)
                .set(DAILY_ATTENDANCES.ARRIVAL_TIME, form.arrivalTime)
                .set(DAILY_ATTENDANCES.DEPARTURE_TIME, form.departureTime)
                .set(DAILY_ATTENDANCES.ATTENDANCE_NOTE, form.attendanceNote)
                .set(DAILY_ATTENDANCES.UPDATED_BY, userAuth.sub)
                .where(DAILY_ATTENDANCES.PUBLIC_ID.eq(id))
                .and(DAILY_ATTENDANCES.DELETED_AT.isNull)
                .execute()

        if (result <= 0) {
            return Result.failure(BusinessLogicException("Não foi possível atualizar a presença diária"))
        }
        return Result.success(Unit)
    }

    override fun delete(id: UUID): Result<Unit> {
        val user = currentUserService.getLoggedUser().getOrElse { return Result.failure(it) }
        val rowsAffected =
            dsl
                .update(DAILY_ATTENDANCES)
                .set(DAILY_ATTENDANCES.UPDATED_BY, user.sub)
                .set(DAILY_ATTENDANCES.DELETED_AT, LocalDateTime.now())
                .where(DAILY_ATTENDANCES.PUBLIC_ID.eq(id))
                .and(DAILY_ATTENDANCES.DELETED_AT.isNull)
                .execute()

        if (rowsAffected == 0) {
            return Result.failure(BusinessLogicException("Não foi possível deletar a presença diária"))
        }
        return Result.success(Unit)
    }

    private fun query() =
        dsl
            .select()
            .from(DAILY_ATTENDANCES)
            .innerJoin(DAILY_REPORTS)
            .on(DAILY_ATTENDANCES.DAILY_REPORT_ID.eq(DAILY_REPORTS.ID))

    override fun save(entity: DailyAttendanceEntity): Result<EntityId> =
        runCatching {
            val isNew = entity.id.isNew()
            val record = dailyAttendancePersistenceMapper.toRecord(entity).getOrThrow()
            if (!isNew) {
                val updatedRows =
                    dsl
                        .update(DAILY_ATTENDANCES)
                        .set(record)
                        .where(DAILY_ATTENDANCES.ID.eq(entity.id.internalId))
                        .execute()

                if (updatedRows == 0) {
                    throw BusinessLogicException("Não foi possível atualizar a presença diária")
                }
                return@runCatching EntityId(entity.id.internalId, entity.id.publicId)
            }
            val result =
                dsl
                    .insertInto(DAILY_ATTENDANCES)
                    .set(record)
                    .returning(DAILY_ATTENDANCES.ID, DAILY_ATTENDANCES.PUBLIC_ID)
                    .fetchOne() ?: throw BusinessLogicException("Não foi possível registrar a presença diária")

            return@runCatching EntityId(result.id!!, result.publicId)
        }
}
