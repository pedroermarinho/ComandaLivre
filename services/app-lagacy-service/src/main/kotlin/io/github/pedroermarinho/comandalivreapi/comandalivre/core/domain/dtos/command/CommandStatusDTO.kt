package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.util.*

data class CommandStatusDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
)
