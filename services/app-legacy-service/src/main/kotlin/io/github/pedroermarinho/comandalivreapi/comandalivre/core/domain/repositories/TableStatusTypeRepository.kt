package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableStatus
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId

interface TableStatusTypeRepository {
    fun getById(statusId: Int): Result<TableStatus>

    fun getByName(statusName: String): Result<TableStatus>

    fun getByKey(key: String): Result<TableStatus>

    fun getAll(pageable: PageableDTO): Result<PageDTO<TableStatus>>

    fun save(entity: TableStatus): Result<EntityId>
}
