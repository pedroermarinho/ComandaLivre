package io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanySummaryResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.user.UserResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Representação de um funcionário.")
data class EmployeeResponse(
    @param:Schema(description = "UUID público do funcionário.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID,
    @param:Schema(description = "Tipo de função.", example = "1")
    val role: RoleTypeResponse,
    @param:Schema(description = "Perfil de usuário associado.", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val user: UserResponse,
    @param:Schema(description = "Restaurante associado.", example = "1")
    val company: CompanySummaryResponse,
    @param:Schema(description = "Indica se o funcionário está ativo.", example = "true")
    val status: Boolean,
    @param:Schema(description = "Data de criação do funcionário.", example = "2023-10-15T10:15:30")
    val createdAt: LocalDateTime,
)
