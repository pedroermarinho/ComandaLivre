package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.FeatureFlagEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

interface FeatureFlagRepository {
    fun getById(id: Int): Result<FeatureFlagEntity>

    fun getById(publicId: UUID): Result<FeatureFlagEntity>

    fun getAll(pageable: PageableDTO): Result<PageDTO<FeatureFlagEntity>>

    fun isFeatureEnabled(keyFlag: String): Result<Boolean>

    fun save(entity: FeatureFlagEntity): Result<EntityId>
}
