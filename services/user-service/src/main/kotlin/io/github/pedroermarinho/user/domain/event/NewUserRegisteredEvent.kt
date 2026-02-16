package io.github.pedroermarinho.user.domain.event

import java.util.*

data class NewUserRegisteredEvent(
    val userId: Int,
    val userPublicId: UUID,
    val sub: String,
    val name: String,
    val email: String,
)
