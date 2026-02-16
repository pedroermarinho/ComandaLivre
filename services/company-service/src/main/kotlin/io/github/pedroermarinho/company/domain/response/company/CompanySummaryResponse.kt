package io.github.pedroermarinho.company.domain.response.company

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Representação de um restaurante.")
data class CompanySummaryResponse(
    @param:Schema(description = "UUID público do restaurante.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID,
    @param:Schema(description = "Nome do restaurante.", example = "Café Central")
    val name: String,
    @param:Schema(description = "Email do restaurante.", example = "contato@cafecentral.com")
    val email: String?,
    @param:Schema(description = "Telefone do restaurante.", example = "+5511999999999")
    val phone: String?,
    @param:Schema(description = "CNPJ do restaurante.", example = "12.345.678/0001-99")
    val cnpj: String?,
    @param:Schema(description = "Tipo de restaurante.", example = "1")
    val type: CompanyTypeResponse,
    @param:Schema(description = "Descrição do restaurante.", example = "Café e restaurante com ambiente acolhedor.")
    val description: String?,
    val settings: CompanySettingsResponse?,
    @param:Schema(description = "Data de criação do restaurante.")
    val createdAt: LocalDateTime,
)
