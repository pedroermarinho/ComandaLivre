package io.github.pedroermarinho.user.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOriginPattern("http://localhost:[*]")
        config.addAllowedOriginPattern("https://api.comandalivre.com.br")
        config.addAllowedOriginPattern("https://admin.comandalivre.com.br")
        config.addAllowedOriginPattern("https://prumodigital.comandalivre.com.br")
        config.addAllowedOriginPattern("https://comandalivre.com.br")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.addAllowedMethod("OPTIONS")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
