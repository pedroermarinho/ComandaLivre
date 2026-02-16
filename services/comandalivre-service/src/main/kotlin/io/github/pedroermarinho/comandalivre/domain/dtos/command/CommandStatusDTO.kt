package io.github.pedroermarinho.comandalivre.domain.dtos.command

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class CommandStatusDTO(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
)
