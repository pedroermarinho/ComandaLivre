package io.github.pedroermarinho.comandalivre.domain.response.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table.TableResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação resumida de uma comanda.")
data class CommandSummaryResponse(
    @param:Schema(description = "ID público da comanda.")
    val id: UUID,
    @param:Schema(description = "Nome ou identificador da comanda.", example = "Mesa 1")
    val name: String,
    @param:Schema(description = "Número de pessoas associadas à comanda.", example = "4")
    val numberOfPeople: Int,
    @param:Schema(description = "Valor total da comanda.", example = "200.50")
    val totalAmount: BigDecimal?,
    @param:Schema(description = "Status atual da comanda.")
    val status: CommandStatusResponse,
    @param:Schema(description = "Mesa associada à comanda.")
    val table: TableResponse,
    val cancellationReason: String?,
    val discountAmount: BigDecimal?,
    val discountDescription: String?,
    @param:Schema(description = "Data e hora de criação da comanda.")
    val createdAt: LocalDateTime,
)
