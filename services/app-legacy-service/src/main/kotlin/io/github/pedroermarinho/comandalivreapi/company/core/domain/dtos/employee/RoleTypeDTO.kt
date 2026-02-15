package io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class RoleTypeDTO(
    val id: EntityId,
    val key: String,
    val name: String,
)
