package io.github.pedroermarinho.user.infra.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    private val securitySchemeName = "BearerAuth"

    @Bean
    fun customOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Comanda Livre API")
                    .version("1.0.0")
                    .description("Documentação dos Módulos da API. Para usar os endpoints protegidos, utilize um token Firebase JWT."),
            ).components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Insira seu token Firebase JWT gerado após login."),
                    ),
            ).addSecurityItem(SecurityRequirement().addList(securitySchemeName))

    @Bean
    fun comandaLivreApiGroup(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("ComandaLivre")
            .packagesToScan("io.github.pedroermarinho.comandalivreapi.comandalivre.core.presenter.controllers")
            .build()

    @Bean
    fun prumoDigitalApiGroup(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("PrumoDigital")
            .packagesToScan("io.github.pedroermarinho.comandalivreapi.prumodigital.core.presenter.controllers")
            .build()

    @Bean
    fun companyApiGroup(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("Company")
            .packagesToScan("io.github.pedroermarinho.comandalivreapi.company.core.presenter.controllers")
            .build()

    @Bean
    fun sharedApiGroup(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("Shared")
            .packagesToScan("io.github.pedroermarinho.user.presenter.controllers")
            .build()
}
