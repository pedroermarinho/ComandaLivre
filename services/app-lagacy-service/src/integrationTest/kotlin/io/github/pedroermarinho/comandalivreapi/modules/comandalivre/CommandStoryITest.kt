package io.github.pedroermarinho.comandalivreapi.modules.comandalivre

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.CommandStatusEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CommandStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    private lateinit var ownerUser: TestObjectFactory.UserInfo
    private lateinit var companyId: UUID
    private lateinit var tableId: UUID
    private lateinit var waiterEmployeeId: String
    private lateinit var commandId: UUID
    private lateinit var inviteeUser: TestObjectFactory.UserInfo

    @BeforeAll
    fun setupAll() {
        ownerUser = TestObjectFactory.createTestUser(::setupJwt)
        val company = TestObjectFactory.createCompany(::setupJwt, ownerUser)
        companyId = company.id
        val table = TestObjectFactory.createTable(::setupJwt, ownerUser, companyId)
        tableId = table.id

        // Create a waiter employee
        inviteeUser = TestObjectFactory.createTestUser(::setupJwt)
        val invite = TestObjectFactory.inviteEmployee(::setupJwt, ownerUser, companyId, RoleTypeEnum.WAITER, inviteeUser.email)
        TestObjectFactory.acceptInvite(::setupJwt, inviteeUser, invite.id)

        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        waiterEmployeeId =
            authenticatedRequest()
                .getWithAuth("/api/v1/company/employees/by-company/{companyId}", companyId)
                .expectSuccess()
                .extract()
                .path<String>("id")

        // Create a command
        setupJwt(inviteeUser.sub, inviteeUser.name, inviteeUser.email)
        val commandForm =
            mapOf(
                "name" to "Comanda Teste",
                "numberOfPeople" to 2,
                "tableId" to tableId.toString(),
                "employeeId" to waiterEmployeeId,
            )
        commandId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/commands", commandForm)
                .expectCreated()
                .extractLocationId()
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve buscar todas as comandas paginadas")
    fun `etapa 1 - deve buscar todas as comandas paginadas`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .getWithAuth("/api/v1/comandalivre/commands")
            .expectSuccess()
            .body("content.size()", greaterThanOrEqualTo(1))
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve buscar comandas filtradas por status")
    fun `etapa 2 - deve buscar comandas filtradas por status`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .queryParam("status", CommandStatusEnum.OPEN.name)
            .getWithAuth("/api/v1/comandalivre/commands")
            .expectSuccess()
            .body("content.size()", greaterThanOrEqualTo(1))
            .body("content[0].status.key", equalTo(CommandStatusEnum.OPEN.name.lowercase()))
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve buscar comanda por ID")
    fun `etapa 3 - deve buscar comanda por ID`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{commandId}", commandId)
            .expectSuccess()
            .body("id", equalTo(commandId.toString()))
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve retornar 404 para comanda inexistente")
    fun `etapa 4 - deve retornar 404 para comanda inexistente`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{commandId}", UUID.randomUUID())
            .statusCode(404)
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Deve retornar 401 ao buscar contagem de comandas sem permissão")
    fun `etapa 5 - deve retornar 401 ao buscar contagem de comandas sem permissao`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email, featureFlag = false)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/count")
            .statusCode(401)
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Deve alterar o status da comanda para CLOSED")
    fun `etapa 7 - deve alterar o status da comanda para CLOSED`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)

        authenticatedRequest()
            .patchWithAuth(
                "/api/v1/comandalivre/commands/{commandId}/status",
                mapOf(
                    "status" to CommandStatusEnum.PAYING.name,
                    "closeAll" to true,
                ),
                commandId,
            ).expectSuccess()

        val changeStatusRequest =
            mapOf(
                "status" to CommandStatusEnum.CLOSED.name,
                "closeAll" to true,
            )
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{commandId}/status", changeStatusRequest, commandId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{commandId}", commandId)
            .expectSuccess()
            .body("status.key", equalTo(CommandStatusEnum.CLOSED.name.lowercase()))
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Deve alterar a mesa da comanda")
    fun `etapa 8 - deve alterar a mesa da comanda`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val newTable = TestObjectFactory.createTable(::setupJwt, ownerUser, companyId)
        val changeTableRequest =
            mapOf(
                "newTableId" to newTable.id.toString(),
            )

        authenticatedRequest()
            .patchWithAuth(
                "/api/v1/comandalivre/commands/{commandId}/status",
                mapOf(
                    "status" to CommandStatusEnum.OPEN.name,
                    "closeAll" to true,
                ),
                commandId,
            ).expectSuccess()

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{commandId}/change-table", changeTableRequest, commandId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{commandId}", commandId)
            .expectSuccess()
            .body("table.id", equalTo(newTable.id.toString()))
    }
}
