package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.NotificationEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
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
