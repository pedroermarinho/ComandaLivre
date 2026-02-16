package io.github.pedroermarinho.prumodigital.domain.request.project

import io.github.pedroermarinho.user.domain.forms.address.AddressForm
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Schema(description = "Formulário para a criação de um novo projeto.")
data class ProjectCreateRequest(
    @field:NotBlank(message = "O nome do projeto é obrigatório.")
    @field:Size(min = 3, max = 255, message = "O nome do projeto deve ter entre 3 e 255 caracteres.")
    @param:Schema(description = "Nome do projeto.", example = "Construção Edifício Atlântico", required = true)
    val name: String,
    @field:NotBlank(message = "O código do projeto é obrigatório.")
    @field:Size(min = 1, max = 50, message = "O código do projeto deve ter entre 1 e 50 caracteres.")
    @param:Schema(description = "Código único do projeto dentro da empresa.", example = "PROJ-ATL-001", required = true)
    val code: String,
    @param:Schema(description = "Nome do cliente para o qual o projeto está sendo executado.", example = "Construtora Mar Aberto")
    val clientName: String?,
    @field:PositiveOrZero(message = "O orçamento deve ser um valor positivo ou zero.")
    @param:Schema(description = "Orçamento estimado ou alocado para o projeto.", example = "5000000.00")
    val budget: BigDecimal?,
    @param:Schema(description = "Data de início planejada para o projeto.", example = "2025-10-01")
    val startDatePlanned: LocalDate?,
    @param:Schema(description = "Data de término planejada para o projeto.", example = "2027-10-01")
    val endDatePlanned: LocalDate?,
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa responsável pelo projeto.", required = true)
    val companyId: UUID,
    @field:Valid
    @param:Schema(description = "Endereço físico principal do projeto.")
    val address: AddressForm?,
)
