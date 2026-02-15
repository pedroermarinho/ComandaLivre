package io.github.pedroermarinho.comandalivreapi.modules.shared

import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

/**
 * Esta classe de teste de integração simula o fluxo de gerenciamento de notificações para um usuário.
 * Ela testa a recuperação de notificações, a contagem de notificações não lidas e a marcação de notificações como lidas.
 * O objetivo é garantir que o sistema de notificações funcione corretamente.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SharedStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    // Variáveis de estado para compartilhar dados entre os testes
    private lateinit var user: TestObjectFactory.UserInfo
    private lateinit var companyId: UUID
    private lateinit var notificationId: UUID

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    @BeforeAll
    fun setupAll() {
        user = TestObjectFactory.createTestUser(::setupJwt)
        val company = TestObjectFactory.createCompany(::setupJwt, user)
        companyId = company.id
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve buscar as notificações do usuário")
    fun `etapa 1 - deve buscar as notificacoes do usuario`() {
        setupJwt(user.sub, user.name, user.email)
        val notifications =
            authenticatedRequest()
                .getWithAuth("/api/v1/shared/notifications")
                .expectSuccess()
                .body("content.size()", greaterThan(0))
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")

        notificationId = UUID.fromString(notifications[0]["id"] as String)
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve contar as notificações não lidas")
    fun `etapa 2 - deve contar as notificacoes nao lidas`() {
        setupJwt(user.sub, user.name, user.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/notifications/unread/count")
            .expectSuccess()
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve marcar notificação como lida")
    fun `etapa 3 - deve marcar notificacao como lida`() {
        setupJwt(user.sub, user.name, user.email)

        authenticatedRequest()
            .putWithAuth("/api/v1/shared/notifications/{id}/read", emptyMap<String, String>(), notificationId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/shared/notifications/unread/count")
            .expectSuccess()
    }
}
