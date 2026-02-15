package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.SessionStatus
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId

interface SessionStatusRepository {
    fun getAll(): Result<List<SessionStatus>>

    fun getByKey(key: String): Result<SessionStatus>

    fun getById(id: Int): Result<SessionStatus>

    fun save(entity: SessionStatus): Result<EntityId>
}
