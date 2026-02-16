package io.github.pedroermarinho.prumodigital.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyActivityEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyActivityForm
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface DailyActivityRepository {
    fun getByDailyReportId(
        dailyReportId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<DailyActivityEntity>>

    fun getById(id: UUID): Result<DailyActivityEntity>

    fun getById(id: Int): Result<DailyActivityEntity>

    fun create(form: DailyActivityForm): Result<EntityId>

    fun update(
        id: UUID,
        form: DailyActivityForm,
    ): Result<Unit>

    fun delete(id: UUID): Result<Unit>

    fun save(entity: DailyActivityEntity): Result<EntityId>
}
