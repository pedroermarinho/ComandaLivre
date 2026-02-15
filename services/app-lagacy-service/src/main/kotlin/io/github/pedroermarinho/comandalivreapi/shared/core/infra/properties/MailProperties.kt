package io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class MailProperties(
    @Value("\${spring.mail.username}") val username: String,
)
