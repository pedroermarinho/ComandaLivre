package io.github.pedroermarinho.company.domain.repositories

import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface RoleTypeRepository {
    fun getByName(value: String): Result<RoleType>

    fun getByKey(key: String): Result<RoleType>

    fun getById(id: Int): Result<RoleType>

    fun getById(id: UUID): Result<RoleType>

    fun getAll(pageable: PageableDTO): Result<PageDTO<RoleType>>

    fun getAll(): Result<List<RoleType>>

    fun save(entity: RoleType): Result<EntityId>
}
