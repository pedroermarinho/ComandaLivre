package io.github.pedroermarinho.comandalivreapi.shared.core.domain.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients.ErrorWebhookClient
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients.EventWebhookClient
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients.UserWebhookClient
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.discord.DiscordMessageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureSystemFlagEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CompanyCreatedEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CriticalErrorOccurredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.CustomSystemEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.event.NewUserRegisteredEvent
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.featureflag.StatusFeatureFlagUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties.DiscordWebhookProperties
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DiscordNotificationListener(
    private val userClient: UserWebhookClient,
    private val eventClient: EventWebhookClient,
    private val errorClient: ErrorWebhookClient,
    private val statusFeatureFlagUseCase: StatusFeatureFlagUseCase,
    private val discordWebhookProperties: DiscordWebhookProperties,
) {
    private val log = KotlinLogging.logger {}

    private fun isWebhookEnabled(): Boolean {
        val isEnabled = statusFeatureFlagUseCase.isEnabled(FeatureSystemFlagEnum.DISCORD_WEBHOOK)

        if (!isEnabled) {
            log.info { "Envio de webhook com Discord desabilitado" }
        }

        if (!discordWebhookProperties.enabled) {
            log.info { "Envio de webhook com Discord desabilitado a nivel de propriedade" }
        }

        return isEnabled && discordWebhookProperties.enabled
    }

    @Async
    @EventListener
    fun onNewUser(event: NewUserRegisteredEvent) {
        if (!isWebhookEnabled()) return

        val content =
            """
            ###################################################################
            📥 **Novo Usuário Registrado**
            • 👤 Nome: ${event.name}
            • 📧 Email: ${event.email}
            • 🆔 ID Público: ${event.userPublicId}
            • 🔑 Sub: ${event.sub}
            ###################################################################
            """.trimIndent()

        userClient.send(DiscordMessageDTO(content = content))
    }

    @Async
    @EventListener
    fun onSystemEvent(event: CustomSystemEvent) {
        if (!isWebhookEnabled()) return

        val icon =
            when (event.level) {
                CustomSystemEvent.EventLevel.INFO -> "📢"
                CustomSystemEvent.EventLevel.WARN -> "⚠️"
                CustomSystemEvent.EventLevel.URGENT -> "🔥"
            }

        val content =
            """
            ###################################################################
            $icon **${event.title}**
            • 📝 Descrição: ${event.description}
            ###################################################################
            """.trimIndent()

        eventClient.send(DiscordMessageDTO(content = content))
    }

    @Async
    @EventListener
    fun onErrorOccurred(event: CriticalErrorOccurredEvent) {
        if (!isWebhookEnabled()) return

        val content =
            """
            ###################################################################
            🚨 **Erro Crítico Detectado**
            • 📍 Local: ${event.location}
            • ❗ Mensagem: ${event.errorMessage}
            ###################################################################
            """.trimIndent()

        errorClient.send(DiscordMessageDTO(content = content))
    }

    @Async
    @EventListener
    fun onCompanyCreated(event: CompanyCreatedEvent) {
        if (!isWebhookEnabled()) return

        log.info { "Listener do Discord acionado para a nova empresa: ${event.companyName}" }

        val content =
            """
            ###################################################################
            🏢 **Nova Empresa Cadastrada!**

            • **Nome da Empresa:** ${event.companyName}
            • **ID Público:** `${event.companyPublicId}`
            • **Email da Empresa:** ${event.companyEmail ?: "Não informado"}

            ---

            • **Criado por:** ${event.ownerName}
            • **Email do Proprietário:** ${event.ownerEmail}
            ###################################################################
            """.trimIndent()

        eventClient.send(DiscordMessageDTO(content = content))
    }
}
