package io.github.pedroermarinho.gateway.config

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.cloud.gateway.route.Route
import java.net.URI

@Component
class LoggingFilter: GlobalFilter, Ordered {

    private val log = KotlinLogging.logger {}


    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain
    ): Mono<Void> {
        val route: Route? = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR)

        val requestUrl: URI? = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)

        if (route != null) {
            log.info{"Redirecionando: ${exchange.request.method} ${exchange.request.path} -> Destino: $requestUrl (Rota ID: ${route.id})"}
        }

        return chain.filter(exchange).then(Mono.fromRunnable {
            val statusCode = exchange.response.statusCode
            log.info{"Resposta do Legado: $statusCode para ${exchange.request.path}"}
        })
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
}