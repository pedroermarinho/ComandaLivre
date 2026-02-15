package io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "firebase.app-check")
data class AppCheckProperties(
    val enabled: Boolean,
    val jwksUrl: String,
    val projectId: String,
    val projectNumber: String,
)
