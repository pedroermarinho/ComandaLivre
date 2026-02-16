package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.GroupEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface GroupRepository {
    fun getByKey(key: String): Result<GroupEntity>

    fun getById(id: UUID): Result<GroupEntity>

    fun getById(id: Int): Result<GroupEntity>

    fun getAll(pageable: PageableDTO): Result<PageDTO<GroupEntity>>

    fun getIdByPublicId(id: UUID): Result<EntityId>

    fun save(entity: GroupEntity): Result<EntityId>
}
