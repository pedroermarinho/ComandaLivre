package io.github.pedroermarinho.comandalivreapi.examples

import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder

/**
 * Exemplo de teste usando as novas utilidades
 * Este arquivo serve como referência para criar novos testes
 */
class ExampleTestWithUtilitiesITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    @Test
    @DisplayName("Exemplo: Criar usuário usando novas utilidades")
    fun `exemplo - criar usuario usando novas utilidades`() {
        // Criar usuário com dados gerados automaticamente
        val user = TestObjectFactory.createTestUser(::setupJwt)

        // Usar extension functions para chamadas HTTP mais limpas
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users/{id}", user.id)
            .expectSuccess()
            .body("id", equalTo(user.id.toString()))
            .body("name", equalTo(user.name))
    }

    @Test
    @DisplayName("Exemplo: Criar empresa de forma simplificada")
    fun `exemplo - criar empresa de forma simplificada`() {
        // Setup: criar usuário
        val owner = TestObjectFactory.createTestUser(::setupJwt)

        // Criar empresa usando factory
        val company = TestObjectFactory.createCompany(::setupJwt, owner)

        // Validar criação
        authenticatedRequest()
            .getWithAuth("/api/v1/company/companies/{id}", company.id)
            .expectSuccess()
            .body("id", equalTo(company.id.toString()))
    }

    @Test
    @DisplayName("Exemplo: Atualizar dados usando novas extensions")
    fun `exemplo - atualizar dados usando novas extensions`() {
        // Setup
        val user = TestObjectFactory.createTestUser(::setupJwt)

        // Atualizar perfil com extension function
        val updateForm = mapOf("name" to "Novo Nome")

        authenticatedRequest()
            .patchWithAuth("/api/v1/shared/users", updateForm)
            .expectSuccess()

        // Verificar atualização
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users/{id}", user.id)
            .expectSuccess()
            .body("name", equalTo("Novo Nome"))
    }
}
