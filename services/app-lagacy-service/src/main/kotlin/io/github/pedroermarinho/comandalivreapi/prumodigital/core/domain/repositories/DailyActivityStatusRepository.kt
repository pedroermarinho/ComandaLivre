package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.DailyActivityStatusEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface DailyActivityStatusRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<DailyActivityStatusEntity>>

    fun getAll(): Result<List<DailyActivityStatusEntity>>

    fun getById(id: UUID): Result<DailyActivityStatusEntity>

    fun getById(id: Int): Result<DailyActivityStatusEntity>

    fun getByKey(key: String): Result<DailyActivityStatusEntity>

    fun save(entity: DailyActivityStatusEntity): Result<EntityId>
}
