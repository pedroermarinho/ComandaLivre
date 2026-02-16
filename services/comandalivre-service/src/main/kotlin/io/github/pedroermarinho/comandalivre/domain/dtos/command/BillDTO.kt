package io.github.pedroermarinho.comandalivre.domain.dtos.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.command.CommandSummaryResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanyResponse

data class BillDTO(
    val command: CommandSummaryResponse,
    val company: CompanyResponse,
    val items: List<BillItemDTO>,
)
