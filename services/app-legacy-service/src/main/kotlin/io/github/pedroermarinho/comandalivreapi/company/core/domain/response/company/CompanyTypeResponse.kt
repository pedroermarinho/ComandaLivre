package io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Representação de um tipo de restaurante.")
data class CompanyTypeResponse(
    @param:Schema(description = "ID do tipo de restaurante.", example = "1")
    val id: UUID,
    @param:Schema(description = "Nome do tipo de restaurante.", example = "cafeteria")
    val name: String,
    @param:Schema(description = "Chave identificadora do tipo de restaurante.", example = "cafeteria_key")
    val key: String,
)
