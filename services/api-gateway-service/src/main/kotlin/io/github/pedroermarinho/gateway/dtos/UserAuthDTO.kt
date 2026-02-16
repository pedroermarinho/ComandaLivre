package io.github.pedroermarinho.gateway.dtos

data class UserAuthDTO(
    val sub: String,
    val email: String,
    val emailVerified: Boolean,
    val name: String,
    val picture: String?,
)
