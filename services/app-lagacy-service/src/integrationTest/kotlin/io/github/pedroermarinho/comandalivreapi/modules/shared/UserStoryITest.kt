package io.github.pedroermarinho.comandalivreapi.modules.shared

import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

/**
 * Esta classe de teste de integração simula o fluxo de gerenciamento de usuários.
 * Ela testa a autenticação de usuários, atualização de perfil, e busca de usuários por ID e e-mail.
 * Também inclui testes para endpoints que exigem permissões específicas, esperando respostas de acesso negado.
 * O objetivo é garantir a funcionalidade básica de usuário e a aplicação de regras de permissão.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    private lateinit var userId: UUID
    private val userEmail = "test.user@example.com"
    private var userName = "Usuário de Teste"

    @Test
    @DisplayName("Configuração do Teste - Mock JWT Decoder")
    @Order(1)
    fun setupTest() {
        setupJwt(name = userName, email = userEmail)
    }

    @Test
    @DisplayName("Deve autenticar o usuário e retornar o perfil")
    @Order(2)
    fun `deve autenticar usuario e retornar perfil`() {
        userId =
            authenticatedRequest()
                .postWithAuth("/api/v1/shared/users/auth", emptyMap<String, String>())
                .expectSuccess()
                .body("email", equalTo(userEmail))
                .body("name", equalTo(userName))
                .body("id", notNullValue())
                .extractId()
    }

    @Test
    @DisplayName("Deve atualizar o perfil de usuário")
    @Order(3)
    fun `deve atualizar perfil de usuario`() {
        val newName = "Novo Nome de Teste"
        val updateUserForm = mapOf("name" to newName)

        authenticatedRequest()
            .patchWithAuth("/api/v1/shared/users", updateUserForm)
            .expectSuccess()

        userName = newName
    }

    @Test
    @Order(4)
    @DisplayName("Deve obter usuário por ID")
    fun `deve obter usuario por ID`() {
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users/{id}", userId)
            .expectSuccess()
            .body("id", equalTo(userId.toString()))
            .body("name", equalTo(userName))
    }

    @Test
    @Order(5)
    @DisplayName("Deve obter usuário por e-mail")
    fun `deve obter usuario por email`() {
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users/email/{email}", userEmail)
            .expectSuccess()
            .body("id", equalTo(userId.toString()))
            .body("name", equalTo(userName))
    }

    @Test
    @Order(6)
    @DisplayName("Deve retornar acesso negado ao buscar todos os usuários")
    fun `deve retornar acesso negado ao buscar todos os usuarios`() {
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users")
            .statusCode(401)
    }

    @Test
    @Order(7)
    @DisplayName("Deve retornar acesso negado ao buscar a contagem de usuários")
    fun `deve retornar acesso negado ao buscar a contagem de usuarios`() {
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users/count")
            .statusCode(401)
            .body(notNullValue())
    }

    @Test
    @Order(8)
    @DisplayName("Deve retornar acesso negado ao buscar registros de usuários")
    fun `deve retornar acesso negado ao buscar registros de usuarios`() {
        authenticatedRequest()
            .getWithAuth("/api/v1/shared/users/registrations")
            .statusCode(401)
            .body(notNullValue())
    }
}
