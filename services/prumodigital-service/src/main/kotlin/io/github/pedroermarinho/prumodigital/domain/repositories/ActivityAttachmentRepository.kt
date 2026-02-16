package io.github.pedroermarinho.prumodigital.domain.repositories

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities.ActivityAttachmentEntity
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.ActivityAttachmentForm
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface ActivityAttachmentRepository {
    fun getByDailyActivityId(
        dailyActivityId: Int,
        pageable: PageableDTO,
    ): Result<PageDTO<ActivityAttachmentEntity>>

    fun getById(id: UUID): Result<ActivityAttachmentEntity>

    fun create(form: ActivityAttachmentForm): Result<EntityId>

    fun delete(id: UUID): Result<Unit>

    fun save(entity: ActivityAttachmentEntity): Result<EntityId>
}
