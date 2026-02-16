package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.entities.FeatureFlagEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface FeatureFlagRepository {
    fun getById(id: Int): Result<FeatureFlagEntity>

    fun getById(publicId: UUID): Result<FeatureFlagEntity>

    fun getAll(pageable: PageableDTO): Result<PageDTO<FeatureFlagEntity>>

    fun isFeatureEnabled(keyFlag: String): Result<Boolean>

    fun save(entity: FeatureFlagEntity): Result<EntityId>
}
