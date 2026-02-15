package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import java.time.LocalDateTime

data class EntityAudit(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
    val version: Int,
) {
    companion object {
        fun createNew(): EntityAudit {
            val now = LocalDateTime.now()
            return EntityAudit(
                createdAt = now,
                updatedAt = now,
                version = 1,
            )
        }
    }

    fun update(): EntityAudit {
        if (this.deletedAt != null) throw BusinessLogicException("Não é possível atualizar um dado que está como deletado")
        return this.copy(
            updatedAt = LocalDateTime.now(),
            version = this.version + 1,
        )
    }

    fun delete(): EntityAudit =
        this.copy(
            deletedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            version = this.version + 1,
        )
}