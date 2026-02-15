package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureFilterDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
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
