package io.github.pedroermarinho.comandalivreapi.modules.comandalivre

import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

/**
 * Esta classe de teste de integração simula o fluxo de gerenciamento de sessões de caixa.
 * Ela testa a abertura de uma nova sessão, a recuperação da sessão ativa, a busca por ID,
 * o fechamento da sessão e a recuperação dos dados de fechamento.
 * O objetivo é garantir que as operações de caixa funcionem corretamente.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SessionStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    // Variáveis de estado para compartilhar dados entre os testes
    private lateinit var owner: TestObjectFactory.UserInfo
    private lateinit var companyId: UUID
    private lateinit var sessionId: UUID

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    @BeforeAll
    fun setupAll() {
        owner = TestObjectFactory.createTestUser(::setupJwt)
        val company = TestObjectFactory.createCompany(::setupJwt, owner)
        companyId = company.id
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve iniciar uma nova sessão de caixa")
    fun `etapa 1 - deve iniciar uma nova sessao de caixa`() {
        setupJwt(owner.sub, owner.name, owner.email)
        val startSessionRequest =
            mapOf(
                "companyId" to companyId.toString(),
                "initialValue" to 100.00,
            )

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/sessions/start", startSessionRequest)
            .expectSuccess()
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve obter a sessão de caixa ativa")
    fun `etapa 2 - deve obter a sessao de caixa ativa`() {
        setupJwt(owner.sub, owner.name, owner.email)

        val session =
            authenticatedRequest()
                .apply { queryParam("companyId", companyId) }
                .getWithAuth("/api/v1/comandalivre/sessions/active")
                .expectSuccess()
                .body("status.key", equalTo("OPEN"))
                .extract()
                .jsonPath()
                .getMap<String, Any>(".")

        sessionId = UUID.fromString(session["id"] as String)
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve obter a sessão de caixa por ID")
    fun `etapa 3 - deve obter a sessao de caixa por ID`() {
        setupJwt(owner.sub, owner.name, owner.email)

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/sessions/{sessionId}", sessionId)
            .expectSuccess()
            .body("id", equalTo(sessionId.toString()))
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve fechar a sessão de caixa")
    fun `etapa 4 - deve fechar a sessao de caixa`() {
        setupJwt(owner.sub, owner.name, owner.email)

        val closeSessionRequest =
            mapOf(
                "companyId" to companyId.toString(),
                "countedCash" to 150.75,
                "countedCard" to 320.50,
                "countedPix" to 210.00,
                "countedOthers" to 50.00,
                "observations" to "Fechamento diário com diferença de R$2,00 no caixa.",
            )

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/sessions/close", closeSessionRequest)
            .expectSuccess()
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Deve obter o fechamento da sessão")
    fun `etapa 5 - deve obter o fechamento da sessao`() {
        setupJwt(owner.sub, owner.name, owner.email)

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/sessions/{sessionId}/closing", sessionId)
            .expectSuccess()
            .body("session.id", equalTo(sessionId.toString()))
            .body("finalBalanceDifference", org.hamcrest.Matchers.notNullValue())
    }
}
