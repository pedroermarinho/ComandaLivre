package io.github.pedroermarinho.company.domain.dtos.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.time.LocalDateTime
import java.util.*

data class EmployeeDTO(
    val id: EntityId,
    val role: RoleTypeDTO,
    val user: UserDTO,
    val company: CompanyDTO,
    val status: Boolean,
    val createdAt: LocalDateTime,
)
