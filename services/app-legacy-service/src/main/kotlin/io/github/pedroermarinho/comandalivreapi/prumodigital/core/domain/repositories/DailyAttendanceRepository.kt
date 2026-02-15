package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyAttendanceEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyAttendanceForm
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface DailyAttendanceRepository {
    fun getAll(
        pageable: PageableDTO,
        dailyReportId: Int,
    ): Result<PageDTO<DailyAttendanceEntity>>

    fun getById(id: UUID): Result<DailyAttendanceEntity>

    fun getById(id: Int): Result<DailyAttendanceEntity>

    fun create(form: DailyAttendanceForm): Result<EntityId>

    fun update(
        id: UUID,
        form: DailyAttendanceForm,
    ): Result<Unit>

    fun delete(id: UUID): Result<Unit>

    fun save(entity: DailyAttendanceEntity): Result<EntityId>
}
