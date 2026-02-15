package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ClosingEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId

interface ClosingRepository {
    fun getBySessionId(sessionId: Int): Result<ClosingEntity>

    fun getById(id: Int): Result<ClosingEntity>

    fun save(entity: ClosingEntity): Result<EntityId>
}
