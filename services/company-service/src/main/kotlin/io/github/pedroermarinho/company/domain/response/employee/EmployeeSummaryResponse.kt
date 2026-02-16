package io.github.pedroermarinho.company.domain.response.employee

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Representação de um funcionário.")
data class EmployeeSummaryResponse(
    @param:Schema(description = "UUID público do funcionário.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID,
    @param:Schema(description = "ID do tipo de função.", example = "1")
    val role: RoleTypeResponse,
    @param:Schema(description = "Indica se o funcionário está ativo.", example = "true")
    val status: Boolean,
    @param:Schema(description = "Data de criação do funcionário.", example = "2023-10-15T10:15:30")
    val createdAt: LocalDateTime,
)
