package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.VersionEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.PlatformEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId

interface VersionRepository {
    fun getLatestByPlatform(platform: PlatformEnum): Result<VersionEntity>

    fun save(entity: VersionEntity): Result<EntityId>
}
