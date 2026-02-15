package io.github.pedroermarinho.comandalivreapi.shared.core.domain.event

import java.util.*

data class NewUserRegisteredEvent(
    val userId: Int,
    val userPublicId: UUID,
    val sub: String,
    val name: String,
    val email: String,
)
