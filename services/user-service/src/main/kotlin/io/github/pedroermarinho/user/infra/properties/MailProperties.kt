package io.github.pedroermarinho.user.infra.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class MailProperties(
    @Value("\${spring.mail.username}") val username: String,
)
