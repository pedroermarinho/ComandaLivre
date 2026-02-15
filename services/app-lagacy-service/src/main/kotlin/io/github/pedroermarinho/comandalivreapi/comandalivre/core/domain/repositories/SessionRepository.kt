package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.SessionEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.UUID

interface SessionRepository {
    fun getByStatus(
        companyId: Int,
        statusId: Int,
    ): Result<SessionEntity>

    fun getById(id: Int): Result<SessionEntity>

    fun getById(id: UUID): Result<SessionEntity>

    fun save(entity: SessionEntity): Result<EntityId>
}
