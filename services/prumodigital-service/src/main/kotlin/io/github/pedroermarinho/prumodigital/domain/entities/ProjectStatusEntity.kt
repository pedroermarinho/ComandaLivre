package io.github.pedroermarinho.prumodigital.domain.entities

import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class ProjectStatusEntity(
    val id: EntityId,
    val key: String,
    val name: String,
    val description: String?,
    val audit: EntityAudit,
) {
    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
