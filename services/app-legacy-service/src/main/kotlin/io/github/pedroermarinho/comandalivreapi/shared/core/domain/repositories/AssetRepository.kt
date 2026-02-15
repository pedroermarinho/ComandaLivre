package io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.AssetEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

interface AssetRepository {
    fun getById(id: UUID): Result<AssetEntity>

    fun getById(id: Int): Result<AssetEntity>

    fun save(entity: AssetEntity): Result<EntityId>
}
