package io.github.pedroermarinho.gateway.config

import org.springframework.boot.web.servlet.AbstractFilterRegistrationBean
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.RegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter




@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Order(1)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            cors { }
            authorizeHttpRequests {
                authorize("/swagger-ui.html", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/docs/**", permitAll)
                authorize("/actuator/**", permitAll)

                authorize(HttpMethod.GET, "/api/v1/company/companies", permitAll)
                authorize(HttpMethod.GET, "/api/v1/company/companies/*", permitAll)
                authorize(HttpMethod.GET, "/api/v1/comandalivre/products", permitAll)
                authorize(HttpMethod.GET, "/api/v1/comandalivre/products/*", permitAll)
                authorize(HttpMethod.POST, "/api/v1/shared/app-versions", permitAll)
                authorize(HttpMethod.GET, "/api/v1/shared/app-versions/*", permitAll)

                authorize(HttpMethod.OPTIONS, "/**", permitAll)

                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = jwtAuthenticationConverter()
                }
            }
        }
        return http.build()
    }

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtGrantedAuthoritiesConverter()
        converter.setAuthoritiesClaimName("roles")
        converter.setAuthorityPrefix("ROLE_")

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter)
        return jwtAuthenticationConverter
    }
}
