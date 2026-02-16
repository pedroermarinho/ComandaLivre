package io.github.pedroermarinho.prumodigital.domain.request.dailyactivity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class MyActivityRequest(
    @field:Schema(description = "ID público do projeto ao qual a atividade pertence.", example = "00000000-0000-0000-0000-000000000000")
    @get:NotNull
    val projectId: UUID,
    @field:Schema(description = "Descrição detalhada da atividade realizada.", example = "Instalação da fiação elétrica no 2º andar.")
    @get:NotBlank
    val description: String,
    @field:Schema(description = "Chave do status da atividade (ex: 'in_progress', 'completed').", example = "in_progress")
    @get:NotBlank
    val statusKey: String,
    @field:Schema(description = "Descrição do local específico da atividade (opcional).", example = "Bloco C, Apartamento 201")
    val location: String?,
)
