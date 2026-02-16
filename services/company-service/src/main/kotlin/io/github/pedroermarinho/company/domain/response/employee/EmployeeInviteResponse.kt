package io.github.pedroermarinho.company.domain.response.employee

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.company.CompanySummaryResponse
import io.github.pedroermarinho.user.domain.response.user.UserResponse
import java.time.LocalDateTime
import java.util.*

data class EmployeeInviteResponse(
    val id: UUID,
    val token: UUID,
    val user: UserResponse?,
    val company: CompanySummaryResponse,
    val role: RoleTypeResponse,
    val createdAt: LocalDateTime,
)
