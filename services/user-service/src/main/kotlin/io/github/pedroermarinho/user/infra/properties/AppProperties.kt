package io.github.pedroermarinho.user.infra.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val name: String = "Comanda Livre API",
    val version: String = "1.0.0",
    val cors: List<String> = listOf("*"),
    val frontendUrl: String = "http://localhost:3000",
    val defaultAdminEmails: String = "",
    val environment: String = "dev",
)
