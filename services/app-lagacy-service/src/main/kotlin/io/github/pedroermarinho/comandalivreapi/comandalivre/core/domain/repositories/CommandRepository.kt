package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandFilterDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.CommandEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

interface CommandRepository {
    fun getById(id: UUID): Result<CommandEntity>

    fun getById(id: Int): Result<CommandEntity>

    fun getAll(
        pageable: PageableDTO,
        filter: CommandFilterDTO,
    ): Result<PageDTO<CommandEntity>>

    fun getAllList(
        companyId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        statusId: Int,
    ): Result<List<CommandEntity>>

    fun getIdById(id: UUID): Result<EntityId>

    fun count(): Result<Long>

    fun existsByTableIdAndStatusIn(
        tableId: TableId,
        statusKeys: List<CommandStatusEnum>,
    ): Boolean

    fun exists(id: UUID): Boolean

    fun save(entity: CommandEntity): Result<EntityId>
}
