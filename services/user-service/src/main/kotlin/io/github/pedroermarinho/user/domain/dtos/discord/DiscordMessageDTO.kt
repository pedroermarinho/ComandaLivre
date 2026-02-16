package io.github.pedroermarinho.user.domain.dtos.discord

data class DiscordMessageDTO(
    val content: String,
    val username: String = "Comanda Livre",
    val avatar_url: String? = null,
)
