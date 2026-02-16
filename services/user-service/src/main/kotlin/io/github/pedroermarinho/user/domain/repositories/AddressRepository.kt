package io.github.pedroermarinho.user.domain.repositories

import io.github.pedroermarinho.user.domain.entities.AddressEntity
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.UUID

interface AddressRepository {
    fun getById(id: Int): Result<AddressEntity>

    fun getById(id: UUID): Result<AddressEntity>

    fun save(entity: AddressEntity): Result<EntityId>
}
