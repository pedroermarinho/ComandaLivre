package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.EventLogEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId

interface EventLogRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<EventLogEntity>>

    fun save(entity: EventLogEntity): Result<EntityId>
}
