package io.github.pedroermarinho.comandalivre.domain.response.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.table.TableResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeSummaryResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de uma comanda.")
data class CommandResponse(
    @param:Schema(description = "ID público da comanda.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID,
    @param:Schema(description = "Funcionário responsável pela comanda.")
    val employee: EmployeeSummaryResponse,
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
    @param:Schema(description = "Data e hora de criação da comanda.")
    val createdAt: LocalDateTime,
    @param:Schema(description = "Motivo do cancelamento da comanda, se aplicável.")
    val cancellationReason: String?,
    @param:Schema(description = "Valor total do desconto aplicado à comanda.")
    val discountAmount: BigDecimal?,
    @param:Schema(description = "Descrição do motivo ou origem do desconto.")
    val discountDescription: String?,
)
