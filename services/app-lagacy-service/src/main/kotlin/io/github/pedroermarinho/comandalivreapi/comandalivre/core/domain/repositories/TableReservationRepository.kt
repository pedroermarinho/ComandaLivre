package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.TableReservationEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.time.LocalDateTime
import java.util.UUID

interface TableReservationRepository {
    fun getById(publicId: UUID): Result<TableReservationEntity>

    fun getByTableIdAndPeriod(
        tableId: Int,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Result<List<TableReservationEntity>>

    fun save(entity: TableReservationEntity): Result<EntityId>
}
