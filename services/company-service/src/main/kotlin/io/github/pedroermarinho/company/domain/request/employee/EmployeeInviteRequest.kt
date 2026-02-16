package io.github.pedroermarinho.company.domain.request.employee

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "Formulário para convidar um novo funcionário para uma empresa.")
data class EmployeeInviteRequest(
    @field:NotBlank(message = "O email do convidado é obrigatório.")
    @field:Email(message = "O formato do email é inválido.")
    @param:Schema(description = "Email da pessoa a ser convidada.", example = "novo.funcionario@email.com", required = true)
    val email: String,
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa que está convidando.", required = true)
    val companyId: UUID,
    @field:NotNull(message = "O ID do cargo é obrigatório.")
    @param:Schema(description = "ID público do cargo que o funcionário ocupará.", required = true)
    val roleId: UUID,
)
