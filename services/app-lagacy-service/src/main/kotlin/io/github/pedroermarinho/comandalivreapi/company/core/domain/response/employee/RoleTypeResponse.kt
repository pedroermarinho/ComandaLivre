package io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação de um tipo de função.")
data class RoleTypeResponse(
    @param:Schema(description = "ID do tipo de função.", example = "1")
    val id: UUID,
    @param:Schema(description = "Nome do tipo de função.", example = "manager")
    val name: String,
    @param:Schema(description = "Chave identificadora do tipo de função.", example = "manager_key")
    val key: String,
)
