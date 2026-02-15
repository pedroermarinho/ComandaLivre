package io.github.pedroermarinho.comandalivreapi.shared.core.domain.request.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Future
import java.time.LocalDateTime

@Schema(description = "Formulário para associar um usuário a um grupo de permissões.")
data class AssignUserToGroupRequest(
    @param:Schema(description = "Observações ou justificativa para a associação.", example = "Acesso concedido para o período de teste.")
    val notes: String?,
    @field:Future(message = "A data de expiração deve ser no futuro.")
    @param:Schema(description = "Data e hora em que a associação do usuário ao grupo expira automaticamente.")
    val expiresAt: LocalDateTime? = null,
)
