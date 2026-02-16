package io.github.pedroermarinho.prumodigital.domain.response.project

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanyResponse
import io.github.pedroermarinho.user.domain.response.address.AddressResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Representação detalhada de um projeto.")
data class ProjectResponse(
    @param:Schema(description = "ID público do projeto.")
    val id: UUID,
    val company: CompanyResponse,
    @param:Schema(description = "Nome do projeto.")
    val name: String,
    @param:Schema(description = "Código único do projeto.")
    val code: String,
    @param:Schema(description = "Nome do cliente.")
    val clientName: String?,
    @param:Schema(description = "Orçamento do projeto.")
    val budget: BigDecimal?,
    @param:Schema(description = "Data de início planejada.")
    val plannedStartDate: LocalDate?,
    @param:Schema(description = "Data de término planejada.")
    val plannedEndDate: LocalDate?,
    @param:Schema(description = "Data de início real.")
    val actualStartDate: LocalDate?,
    @param:Schema(description = "Data de término real.")
    val actualEndDate: LocalDate?,
    @param:Schema(description = "Status atual do projeto.")
    val status: ProjectStatusResponse,
    @param:Schema(description = "Descrição do projeto.")
    val description: String?,
    @param:Schema(description = "Endereço do projeto.")
    val address: AddressResponse?,
    @param:Schema(description = "Data e hora de criação do projeto.")
    val createdAt: LocalDateTime,
)
