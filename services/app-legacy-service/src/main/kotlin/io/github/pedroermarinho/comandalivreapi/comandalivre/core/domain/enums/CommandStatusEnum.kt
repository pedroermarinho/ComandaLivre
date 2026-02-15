package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command.CommandStatusDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject.CommandStatus
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

enum class CommandStatusEnum(
    val value: String,
) {
    OPEN("open"),
    CLOSED("closed"),
    CANCELED("canceled"),
    PAYING("paying"),
    PARTIALLY_PAID("partially_paid"),
    ;

    fun matches(value: CommandStatusDTO): Boolean = this.value == value.key

    companion object {
        fun from(value: CommandStatus): CommandStatusEnum =
            entries.firstOrNull { it.value == value.key.value }
                ?: throw BusinessLogicException("Status desconhecido: ${value.key}")
    }

    fun canTransitionTo(newStatus: CommandStatusEnum): Boolean {
        if (this == newStatus) return false
        return when (this) {
            OPEN -> newStatus in setOf(PAYING, CANCELED)
            PAYING -> newStatus in setOf(PARTIALLY_PAID, CLOSED)
            PARTIALLY_PAID -> newStatus in setOf(PAYING, CLOSED)
            CLOSED, CANCELED -> newStatus == OPEN
        }
    }
}
