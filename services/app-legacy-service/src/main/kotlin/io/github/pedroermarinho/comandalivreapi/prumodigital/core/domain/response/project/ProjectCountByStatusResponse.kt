package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.project

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Contagem de projetos por status.")
data class ProjectCountByStatusResponse(
    @param:Schema(description = "Status do projeto.")
    val status: ProjectStatusResponse,
    @param:Schema(description = "Quantidade de projetos com este status.")
    val count: Long,
)
