package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.EventLogEntity
import io.github.pedroermarinho.shared.valueobject.EntityId

interface EventLogRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<EventLogEntity>>

    fun save(entity: EventLogEntity): Result<EntityId>
}
