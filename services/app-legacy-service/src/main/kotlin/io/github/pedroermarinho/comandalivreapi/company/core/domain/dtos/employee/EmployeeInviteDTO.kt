package io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class EmployeeInviteDTO(
    val id: EntityId,
    val token: UUID,
    val expirationDate: LocalDate,
    val email: String,
    val user: UserDTO?,
    val company: CompanyDTO,
    val role: RoleTypeDTO,
    val status: EmployeeInviteStatusDTO,
    val createdAt: LocalDateTime,
)
