package io.github.pedroermarinho.shared.dtos.user

data class UserAuthDTO(
    val sub: String,
    val email: String,
    val emailVerified: Boolean,
    val name: String,
    val picture: String?,
)
