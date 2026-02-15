package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import java.util.UUID

data class CommandFilterDTO(
    val tableId: UUID? = null,
    val companyId: UUID? = null,
    val status: List<CommandStatusEnum>? = null,
)
