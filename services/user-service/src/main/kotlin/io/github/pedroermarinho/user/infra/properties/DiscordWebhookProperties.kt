package io.github.pedroermarinho.user.infra.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "discord.webhooks")
data class DiscordWebhookProperties(
    val enabled: Boolean,
    val user: String,
    val event: String,
    val error: String,
)
