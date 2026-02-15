package io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company

import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class CompanyTypeDTO(
    val id: EntityId,
    val key: String,
    val name: String,
)
