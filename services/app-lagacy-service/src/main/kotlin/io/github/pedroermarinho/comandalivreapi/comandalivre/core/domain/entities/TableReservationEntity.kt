package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableId
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.TableReservationStatus
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserId
import java.time.LocalDateTime
import java.util.UUID

data class TableReservationEntity(
    val id: EntityId,
    val tableId: TableId,
    val reservedFor: String?,
    val reservedForUserId: UserId?,
    val reservationStart: LocalDateTime,
    val reservationEnd: LocalDateTime?,
    val status: TableReservationStatus,
    val notes: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            tableId: Int,
            reservedFor: String?,
            reservedForUserId: Int?,
            reservationStart: LocalDateTime,
            reservationEnd: LocalDateTime?,
            status: TableReservationStatus,
            notes: String?,
        ): TableReservationEntity =
            TableReservationEntity(
                id = EntityId.createNew(publicId = publicId),
                tableId = TableId(tableId),
                reservedFor = reservedFor,
                reservedForUserId = reservedForUserId?.let { UserId(it) },
                reservationStart = reservationStart,
                reservationEnd = reservationEnd,
                status = status,
                notes = notes,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun updateStatus(status: TableReservationStatus): TableReservationEntity =
        this.copy(
            status = status,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
