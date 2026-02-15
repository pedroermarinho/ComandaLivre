package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.command

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.table.TableDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.EmployeeDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class CommandDTO(
    val id: EntityId,
    val employee: EmployeeDTO,
    val name: String,
    val numberOfPeople: Int,
    val totalAmount: BigDecimal?,
    val status: CommandStatusDTO,
    val table: TableDTO,
    val cancellationReason: String?,
    val cancelledByUserId: Int?,
    val discountAmount: BigDecimal?,
    val discountDescription: String?,
    val createdAt: LocalDateTime,
)
