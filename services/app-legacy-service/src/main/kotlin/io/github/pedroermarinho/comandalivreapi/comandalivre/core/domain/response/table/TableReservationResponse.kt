package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação de uma reserva de mesa.")
data class TableReservationResponse(
    @param:Schema(description = "ID público da reserva.")
    val id: UUID,
    @param:Schema(description = "ID interno da mesa reservada.")
    val tableId: Int,
    @param:Schema(description = "Nome do cliente para a reserva.")
    val reservedFor: String?,
    @param:Schema(description = "Data e hora de início da reserva.")
    val reservationStart: LocalDateTime,
    @param:Schema(description = "Data e hora de término da reserva.")
    val reservationEnd: LocalDateTime?,
    @param:Schema(description = "ID interno do status da reserva.")
    val statusId: Int,
    @param:Schema(description = "Observações adicionais sobre a reserva.")
    val notes: String?,
)
