package io.github.pedroermarinho.user.domain.event

import java.util.*

data class EmployeeInviteCreatedEvent(
    val recipientEmail: String,
    val companyName: String,
    val roleName: String,
    val inviteToken: UUID,
    val userId: Int?,
)
