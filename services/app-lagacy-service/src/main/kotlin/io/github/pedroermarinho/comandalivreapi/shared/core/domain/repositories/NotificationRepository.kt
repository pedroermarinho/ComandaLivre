package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.NotificationEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.UUID

interface NotificationRepository {
    fun getAll(pageable: PageableDTO): Result<PageDTO<NotificationEntity>>

    fun getByUserId(
        userId: Int,
        pageable: PageableDTO,
    ): Result<PageDTO<NotificationEntity>>

    fun countUnreadByUserId(userId: Int): Result<Long>

    fun save(entity: NotificationEntity): Result<EntityId>

    fun getById(id: UUID): Result<NotificationEntity>
}
