package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName

data class CompanyType(
    val id: EntityId,
    val name: TypeName,
    val key: TypeKey,
    val audit: EntityAudit,
)
