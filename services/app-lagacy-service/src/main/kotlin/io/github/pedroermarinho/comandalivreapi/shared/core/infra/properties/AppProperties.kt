package io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val name: String = "Comanda Livre API",
    val version: String = "1.0.0",
    val cors: List<String> = listOf("*"),
    val frontendUrl: String = "http://localhost:3000",
    val rememberMeKey: String,
    val defaultAdminEmails: String = "",
    val environment: String = "dev",
)
