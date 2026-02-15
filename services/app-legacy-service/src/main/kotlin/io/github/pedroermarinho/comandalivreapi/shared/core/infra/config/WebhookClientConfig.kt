package io.github.pedroermarinho.comandalivreapi.shared.core.infra.config

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients.ErrorWebhookClient
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients.EventWebhookClient
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.clients.UserWebhookClient
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.properties.DiscordWebhookProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class WebhookClientConfig(
    private val props: DiscordWebhookProperties,
) {
    @Bean
    fun userWebhookClient(builder: RestClient.Builder): UserWebhookClient = bind(builder.baseUrl(props.user), UserWebhookClient::class.java)

    @Bean
    fun eventWebhookClient(builder: RestClient.Builder): EventWebhookClient = bind(builder.baseUrl(props.event), EventWebhookClient::class.java)

    @Bean
    fun errorWebhookClient(builder: RestClient.Builder): ErrorWebhookClient = bind(builder.baseUrl(props.error), ErrorWebhookClient::class.java)

    private fun <T> bind(
        builder: RestClient.Builder,
        serviceClass: Class<T>,
    ): T {
        val restClient = builder.build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(serviceClass)
    }
}
