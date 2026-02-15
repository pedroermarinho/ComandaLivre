package io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.discord.DiscordMessageDTO
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface UserWebhookClient {
    @PostExchange
    fun send(
        @RequestBody message: DiscordMessageDTO,
    )
}
