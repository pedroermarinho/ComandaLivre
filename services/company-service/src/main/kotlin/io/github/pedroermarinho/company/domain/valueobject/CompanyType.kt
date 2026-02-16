package io.github.pedroermarinho.company.domain.valueobject

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName

data class CompanyType(
    val id: EntityId,
    val name: TypeName,
    val key: TypeKey,
    val audit: EntityAudit,
)
