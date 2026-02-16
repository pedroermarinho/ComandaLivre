package io.github.pedroermarinho.comandalivre.domain.request.table

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.UUID

@Schema(
    name = "CreateTableReservationRequest",
    description = "Formulário para criar uma nova reserva de mesa.",
)
data class CreateTableReservationRequest(
    @field:NotNull(message = "O ID da mesa é obrigatório.")
    @param:Schema(description = "ID público da mesa a ser reservada.", required = true)
    val tableId: UUID,
    @param:Schema(description = "Nome do cliente para a reserva (caso não seja um usuário do sistema).")
    val reservedFor: String?,
    @param:Schema(description = "ID público do usuário para o qual a mesa está sendo reservada (se aplicável).")
    val reservedForUserId: UUID?,
    @field:NotNull(message = "A data e hora de início da reserva são obrigatórias.")
    @field:Future(message = "A data de início da reserva deve ser no futuro.")
    @param:Schema(description = "Data e hora de início da reserva.", required = true)
    val reservationStart: LocalDateTime,
    @param:Schema(description = "Data e hora de término da reserva (opcional).")
    val reservationEnd: LocalDateTime?,
    @field:NotBlank(message = "A chave de status da reserva é obrigatória.")
    @param:Schema(description = "Chave (key) do status inicial da reserva (ex: \"CONFIRMED\").", required = true)
    val statusKey: String,
    @param:Schema(description = "Observações ou notas adicionais sobre a reserva.")
    val notes: String?,
)
