package io.github.pedroermarinho.comandalivre.domain.valueobject

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName

data class SessionStatus(
    val id: EntityId,
    val key: TypeKey,
    val name: TypeName,
    val description: String?,
    val audit: EntityAudit,
)
