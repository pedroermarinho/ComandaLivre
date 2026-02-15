package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId

interface CommandStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<CommandStatus>>

    fun getById(statusId: Int): Result<CommandStatus>

    fun getByName(statusName: String): Result<CommandStatus>

    fun getByKey(key: String): Result<CommandStatus>

    fun save(entity: CommandStatus): Result<EntityId>
}
