package io.github.pedroermarinho.user.infra.config

import io.github.pedroermarinho.user.domain.clients.ErrorWebhookClient
import io.github.pedroermarinho.user.domain.clients.EventWebhookClient
import io.github.pedroermarinho.user.domain.clients.UserWebhookClient
import io.github.pedroermarinho.user.infra.properties.DiscordWebhookProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

@Configuration
class WebhookClientConfig(
    private val props: DiscordWebhookProperties,
) {
    @Bean
    fun restClientBuilder(): RestClient.Builder {
        return RestClient.builder()
    }

    @Bean
    fun userWebhookClient(builder: RestClient.Builder): UserWebhookClient =
        bind(builder.baseUrl(props.user))

    @Bean
    fun eventWebhookClient(builder: RestClient.Builder): EventWebhookClient =
        bind(builder.baseUrl(props.event))

    @Bean
    fun errorWebhookClient(builder: RestClient.Builder): ErrorWebhookClient =
        bind(builder.baseUrl(props.error))

    private inline fun <reified T : Any> bind(builder: RestClient.Builder): T {
        val restClient = builder.build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient<T>()
    }
}
