package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.cashregister

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDateTime

data class SessionDTO(
    val id: EntityId,
    val employee: EmployeeDTO,
    val openedByUser: UserDTO?,
    val closedByUser: UserDTO?,
    val initialValue: BigDecimal,
    val status: SessionStatusDTO,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val notes: String?,
    val createdAt: LocalDateTime,
)
