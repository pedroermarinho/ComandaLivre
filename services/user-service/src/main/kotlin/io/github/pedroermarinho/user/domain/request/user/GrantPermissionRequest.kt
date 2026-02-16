package io.github.pedroermarinho.user.domain.request.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "Formulário para conceder uma permissão (feature) a um grupo.")
data class GrantPermissionRequest(
    @field:NotNull(message = "O ID do grupo é obrigatório.")
    @param:Schema(description = "ID público do grupo que receberá a permissão.", required = true)
    val groupId: UUID,
    @field:NotNull(message = "O ID da feature é obrigatório.")
    @param:Schema(description = "ID público da feature a ser concedida ao grupo.", required = true)
    val featureId: UUID,
)
