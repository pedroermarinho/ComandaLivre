package io.github.pedroermarinho.comandalivreapi.modules.comandalivre

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
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
class OrderStoryITest : AuthIntegrationTest() {
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
    private lateinit var productId: UUID
    private lateinit var orderId: UUID
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

        // Create a product
        productId = TestObjectFactory.createProduct(::setupJwt, ownerUser, companyId).id

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
    @DisplayName("Etapa 1: Deve adicionar um novo pedido à comanda")
    fun `etapa 1 - deve adicionar um novo pedido a comanda`() {
        setupJwt(inviteeUser.sub, inviteeUser.name, inviteeUser.email)
        val orderCreateRequest =
            mapOf(
                "commandId" to commandId.toString(),
                "items" to
                    listOf(
                        mapOf(
                            "productId" to productId.toString(),
                            "quantity" to 2,
                            "notes" to "Sem cebola",
                        ),
                    ),
            )

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderCreateRequest)
            .expectCreated()

        // Verify the order was created and get its ID
        val orders =
            authenticatedRequest()
                .queryParam("commandId", commandId)
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")

        Assertions.assertTrue(orders.isNotEmpty())
        orderId = UUID.fromString(orders[0]["id"] as String)
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve buscar todos os pedidos paginados")
    fun `etapa 2 - deve buscar todos os pedidos paginados`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .queryParam("commandId", commandId.toString())
            .getWithAuth("/api/v1/comandalivre/orders")
            .expectSuccess()
            .body("content.size()", greaterThanOrEqualTo(1))
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve buscar pedido por ID")
    fun `etapa 3 - deve buscar pedido por ID`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", orderId)
            .expectSuccess()
            .body("id", equalTo(orderId.toString()))
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve alterar o status do pedido")
    fun `etapa 4 - deve alterar o status do pedido`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val changeStatusRequest =
            mapOf(
                "status" to OrderStatusEnum.IN_PREPARATION.value,
            )
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/orders/{id}/status", changeStatusRequest, orderId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", orderId)
            .expectSuccess()
            .body("status.key", equalTo(OrderStatusEnum.IN_PREPARATION.name.lowercase()))
    }

    @Test
    @Order(6)
    @DisplayName("Etapa 6: Deve obter os dados de impressão do pedido")
    fun `etapa 6 - deve obter os dados de impressao do pedido`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}/print-data", orderId)
            .expectSuccess()
            .body("publicId", equalTo(orderId.toString()))
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Deve verificar se a comanda está totalmente fechada (false)")
    fun `etapa 7 - deve verificar se a comanda esta totalmente fechada false`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val isFullyClosed =
            authenticatedRequest()
                .queryParam("commandId", commandId)
                .getWithAuth("/api/v1/comandalivre/orders/is-command-fully-closed")
                .expectSuccess()
                .extract()
                .`as`(Boolean::class.java)

        Assertions.assertFalse(isFullyClosed)
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Deve cancelar o pedido")
    fun `etapa 8 - deve cancelar o pedido`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val cancelRequest =
            mapOf(
                "reason" to "Cliente desistiu",
            )
        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders/$orderId/cancel", cancelRequest)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", orderId)
            .expectSuccess()
            .body("status.key", equalTo(OrderStatusEnum.ITEM_CANCELED.name.lowercase()))
    }

//    @Test
//    @Order(9)
//    @DisplayName("Etapa 9: Deve verificar se a comanda está totalmente fechada (true)")
//    fun `etapa 9 - deve verificar se a comanda esta totalmente fechada true`() {
//        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
//        val isFullyClosed = authenticatedRequest()
//            .queryParam("commandId", commandId)
//            .getWithAuth("/api/v1/comandalivre/orders/is-command-fully-closed")
//            .expectSuccess()
//            .extract()
//            .`as`(Boolean::class.java)
//
//        Assertions.assertFalse(isFullyClosed)
//    }

    @Test
    @Order(10)
    @DisplayName("Etapa 10: Deve remover o pedido")
    fun `etapa 10 - deve remover o pedido`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .deleteWithAuth("/api/v1/comandalivre/orders/{id}", orderId)

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", orderId)
            .statusCode(404)
    }
}
