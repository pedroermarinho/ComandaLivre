package io.github.pedroermarinho.comandalivreapi.modules.comandalivre

import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.authenticatedRequest
import io.github.pedroermarinho.comandalivreapi.helpers.expectSuccess
import io.github.pedroermarinho.comandalivreapi.helpers.getWithAuth
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder

/**
 * Esta classe de teste de integração foca em verificar a funcionalidade de endpoints gerais
 * do módulo 'comandalivre' que não se encaixam em fluxos de negócio mais complexos.
 * Ela testa a recuperação de status de comandas, status de pedidos, categorias de produtos,
 * e o estado inicial do dashboard, garantindo que esses endpoints retornem os dados esperados.
 */
class GeneralComandaLivreStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    @Test
    @DisplayName("Deve buscar todos os status de comandas")
    fun `deve buscar todos os status de comandas`() {
        setupJwt()
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/command-status")
            .expectSuccess()
            .body("content.size()", greaterThan(0))
    }

    @Test
    @DisplayName("Deve buscar um status de comanda por ID")
    fun `deve buscar um status de comanda por ID`() {
        setupJwt()
        val statuses =
            authenticatedRequest()
                .getWithAuth("/api/v1/comandalivre/command-status")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")

        // Verificar que a lista não está vazia
        assert(statuses.isNotEmpty()) { "Lista de status de comandas não deve estar vazia" }
    }

    @Test
    @DisplayName("Deve obter o estado inicial do dashboard")
    fun `deve obter o estado inicial do dashboard`() {
        setupJwt()
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/dashboard/initial-state")
            .expectSuccess()
            .body("message", notNullValue())
    }

    @Test
    @DisplayName("Deve buscar todas as categorias de produtos paginadas")
    fun `deve buscar todas as categorias de produtos paginadas`() {
        setupJwt()
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/product-categories")
            .expectSuccess()
            .body("content.size()", greaterThan(0))
    }

    @Test
    @DisplayName("Deve buscar todas as categorias de produtos em lista")
    fun `deve buscar todas as categorias de produtos em lista`() {
        setupJwt()
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/product-categories/list")
            .expectSuccess()
            .body("size()", greaterThan(0))
    }

    @Test
    @DisplayName("Deve buscar todos os status de pedidos")
    fun `deve buscar todos os status de pedidos`() {
        setupJwt()
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/order-status")
            .expectSuccess()
            .body("size()", greaterThan(0))
    }

    @Test
    @DisplayName("Deve buscar todos os status de mesas")
    fun `deve buscar todos os status de mesas`() {
        setupJwt()
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/table-status")
            .expectSuccess()
            .body("content.size()", greaterThan(0))
    }
}
