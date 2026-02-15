package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user

import java.util.UUID

data class UserFilterDTO(
    val group: UUID? = null,
    val excludeGroup: UUID? = null,
)
