package io.github.pedroermarinho.comandalivre.domain.dtos.command

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Representação de um total de comanda.")
data class TotalizeCommandDTO(
    @Schema(description = "Valor total da comanda.", example = "200.50")
    val total: BigDecimal,
    @Schema(description = "Quantidade de itens na comanda.", example = "5")
    val totalItems: Int,
)
