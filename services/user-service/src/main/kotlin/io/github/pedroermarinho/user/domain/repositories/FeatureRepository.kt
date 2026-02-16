package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.user.domain.dtos.feature.FeatureFilterDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.FeatureEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface FeatureRepository {
    fun getByKey(key: String): Result<FeatureEntity>

    fun getById(id: UUID): Result<FeatureEntity>

    fun getAll(
        pageable: PageableDTO,
        filter: FeatureFilterDTO,
    ): Result<PageDTO<FeatureEntity>>

    fun save(entity: FeatureEntity): Result<EntityId>
}
