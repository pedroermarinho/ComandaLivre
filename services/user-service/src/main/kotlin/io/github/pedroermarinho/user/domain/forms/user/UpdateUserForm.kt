package io.github.pedroermarinho.user.domain.forms.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Formulário apra atualização de perfis de usuário.")
data class UpdateUserForm(
    @field:NotBlank(message = "O nome do usuário é obrigatório.")
    @param:Schema(description = "Nome do usuário.", example = "Pedro Marinho", required = true)
    val name: String,
)
