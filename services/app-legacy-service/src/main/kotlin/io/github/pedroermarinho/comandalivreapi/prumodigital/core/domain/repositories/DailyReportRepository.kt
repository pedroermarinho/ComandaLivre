package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyReportEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.DailyReportForm
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDate
import java.util.*

interface DailyReportRepository {
    fun getAll(
        projectId: UUID,
        pageable: PageableDTO,
    ): Result<PageDTO<DailyReportEntity>>

    fun getById(id: UUID): Result<DailyReportEntity>

    fun getById(id: Int): Result<DailyReportEntity>

    fun create(form: DailyReportForm): Result<EntityId>

    fun update(
        id: UUID,
        form: DailyReportForm,
    ): Result<Unit>

    fun delete(id: UUID): Result<Unit>

    fun existsByProjectIdAndReportDate(
        projectId: Int,
        reportDate: LocalDate,
    ): Result<Boolean>

    fun save(entity: DailyReportEntity): Result<EntityId>
}
