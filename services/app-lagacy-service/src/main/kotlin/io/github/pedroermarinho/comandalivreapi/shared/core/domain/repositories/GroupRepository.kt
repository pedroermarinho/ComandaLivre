package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.GroupEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface GroupRepository {
    fun getByKey(key: String): Result<GroupEntity>

    fun getById(id: UUID): Result<GroupEntity>

    fun getById(id: Int): Result<GroupEntity>

    fun getAll(pageable: PageableDTO): Result<PageDTO<GroupEntity>>

    fun getIdByPublicId(id: UUID): Result<EntityId>

    fun save(entity: GroupEntity): Result<EntityId>
}
