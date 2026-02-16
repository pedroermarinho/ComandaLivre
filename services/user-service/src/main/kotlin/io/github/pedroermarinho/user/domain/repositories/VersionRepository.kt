package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.user.domain.entities.VersionEntity
import io.github.pedroermarinho.user.domain.enums.PlatformEnum
import io.github.pedroermarinho.shared.valueobject.EntityId

interface VersionRepository {
    fun getLatestByPlatform(platform: PlatformEnum): Result<VersionEntity>

    fun save(entity: VersionEntity): Result<EntityId>
}
