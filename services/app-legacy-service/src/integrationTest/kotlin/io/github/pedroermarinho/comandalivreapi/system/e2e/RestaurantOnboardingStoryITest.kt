package io.github.pedroermarinho.comandalivreapi.system.e2e

import com.github.javafaker.Faker
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

/**
 * Esta classe de teste de integração simula o fluxo completo de onboarding de um restaurante
 * e operações diárias de comanda. Ela abrange desde a criação do proprietário e do restaurante,
 * adição de produtos e mesas, convite e aceitação de funcionários, até a criação e gerenciamento
 * de comandas e pedidos. O objetivo é validar a integração de múltiplos módulos e funcionalidades
 * em um cenário de uso realista.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RestaurantOnboardingStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    private val faker = Faker(Locale.forLanguageTag("pt-BR"))

    // Variáveis de estado para compartilhar dados entre os testes
    private lateinit var ownerId: UUID
    private lateinit var ownerSub: String
    private lateinit var ownerEmail: String
    private lateinit var ownerName: String
    private lateinit var companyId: UUID
    private lateinit var companyTypeId: UUID
    private lateinit var productCategoryId: UUID
    private lateinit var tableId: UUID
    private lateinit var waiterRoleId: UUID
    private lateinit var inviteeSub: String
    private lateinit var inviteeEmail: String
    private lateinit var inviteeName: String
    private lateinit var inviteId: UUID
    private lateinit var waiterEmployeeId: UUID
    private lateinit var commandId: UUID

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    @BeforeAll
    fun setupAll() {
        val owner = TestObjectFactory.createTestUser(::setupJwt)
        ownerId = owner.id
        ownerSub = owner.sub
        ownerName = owner.name
        ownerEmail = owner.email

        val company = TestObjectFactory.createCompany(::setupJwt, owner)
        companyId = company.id
        companyTypeId = company.typeId

        val product = TestObjectFactory.createProduct(::setupJwt, owner, companyId)
        productCategoryId = product.categoryId

        val table = TestObjectFactory.createTable(::setupJwt, owner, companyId)
        tableId = table.id
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Proprietário deve convidar um novo funcionário e ele deve aceitar")
    fun `etapa 5 - proprietario deve convidar um novo funcionario e ele deve aceitar`() {
        val invitee = TestObjectFactory.createTestUser(::setupJwt)
        inviteeSub = invitee.sub
        inviteeName = invitee.name
        inviteeEmail = invitee.email

        val owner = TestObjectFactory.UserInfo(ownerId, ownerSub, ownerName, ownerEmail)
        val invite = TestObjectFactory.inviteEmployee(::setupJwt, owner, companyId, RoleTypeEnum.WAITER, inviteeEmail)
        inviteId = invite.id

        TestObjectFactory.acceptInvite(::setupJwt, invitee, inviteId)
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Garçom deve criar uma nova comanda para uma mesa")
    fun `etapa 7 - garcom deve criar uma nova comanda para uma mesa`() {
        // Encontra o ID do funcionário para o garçom
        setupJwt(ownerSub, ownerName, ownerEmail) // Loga como proprietário para ver os funcionários
        val waiterEmployeeId =
            authenticatedRequest()
                .getWithAuth("/api/v1/company/employees/by-company/{companyId}", companyId)
                .expectSuccess()
                .extract()
                .path<String>("id")

        setupJwt(inviteeSub, inviteeName, inviteeEmail)
        val commandForm =
            mapOf(
                "name" to faker.name().fullName(),
                "numberOfPeople" to 2,
                "tableId" to tableId.toString(),
                "employeeId" to waiterEmployeeId.toString(),
            )

        commandId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/commands", commandForm)
                .expectCreated()
                .extractLocationId()
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Garçom deve adicionar produtos à comanda")
    fun `etapa 8 - garcom deve adicionar produtos a comanda`() {
        // Loga como garçom
        setupJwt(inviteeSub, inviteeName, inviteeEmail)

        // Obtém um produto para adicionar
        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productId = UUID.fromString(products.first()["id"] as String)

        val orderItems = listOf(mapOf("productId" to productId.toString(), "notes" to "Sem cebola"))
        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .expectCreated()
    }

    @Test
    @Order(9)
    @DisplayName("Etapa 9: O cliente deve pagar a comanda")
    fun `etapa 9 - o cliente deve pagar a comanda`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // O proprietário pode mudar o status da comanda

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "PAYING"), commandId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{id}", commandId)
            .expectSuccess()
            .body("status.key", equalTo("paying"))
    }

    @Test
    @Order(10)
    @DisplayName("Etapa 10: Garçom deve fechar a comanda e todas as ordens ainda abertas")
    fun `etapa 10 - garcom deve fechar a comanda e todas as ordens ainda abertas`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // O proprietário pode mudar o status da comanda

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "CLOSED", "closeAll" to true), commandId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{id}", commandId)
            .expectSuccess()
            .body("status.key", equalTo("closed"))
    }

    @Test
    @Order(11)
    @DisplayName("Etapa 11: Proprietário deve ser capaz de reabrir uma comanda fechada")
    fun `etapa 11 - proprietario deve ser capaz de reabrir uma comanda fechada`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // O proprietário pode mudar o status da comanda

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "OPEN"), commandId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{id}", commandId)
            .expectSuccess()
            .body("status.key", equalTo("open"))
    }

    @Test
    @Order(12)
    @DisplayName("Etapa 12: Garçom deve ser capaz de trocar a comanda de mesa")
    fun `etapa 12 - garcom deve ser capaz de trocar a comanda de mesa`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // O garçom pode mudar a mesa da comanda

        val newTableForm =
            mapOf(
                "name" to "Mesa Nova ${faker.number().digit()}",
                "numPeople" to faker.number().numberBetween(2, 8),
                "description" to "Mesa para troca",
                "companyId" to companyId.toString(),
            )

        // Cria uma nova mesa para a troca
        val newTableId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/tables", newTableForm)
                .expectCreated()
                .extractLocationId()

        // Troca a comanda para a nova mesa
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/change-table", mapOf("newTableId" to newTableId.toString()), commandId)
            .expectSuccess()

        // Verifica se a comanda foi atualizada para a nova mesa
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{id}", commandId)
            .expectSuccess()
            .body("table.id", equalTo(newTableId.toString()))
    }

    @Test
    @Order(15)
    @DisplayName("Etapa 15: Garçom não deve ser capaz de trocar a comanda para a mesma mesa")
    fun `etapa 15 - garcom nao deve ser capaz de trocar a comanda para a mesma mesa`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // O garçom pode mudar a mesa da comanda

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/change-table", mapOf("newTableId" to tableId.toString()), commandId)
            .expectSuccess()

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/change-table", mapOf("newTableId" to tableId.toString()), commandId)
            .statusCode(400)
            .body("message", equalTo("A comanda já está na mesa de destino."))
    }

    @Test
    @Order(16)
    @DisplayName("Etapa 16: Garçom não deve ser capaz de trocar a comanda para uma mesa de outra empresa")
    fun `etapa 16 - garcom nao deve ser capaz de trocar a comanda para uma mesa de outra empresa`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // O garçom pode mudar a mesa da comanda

        // Cria uma empresa e mesa fictícias para outra empresa
        val otherCompanyForm =
            mapOf(
                "name" to faker.company().name(),
                "email" to faker.internet().emailAddress().replaceAfter("@", "comandalivre.com.br"),
                "phone" to faker.phoneNumber().cellPhone(),
                "cnpj" to null,
                "description" to faker.lorem().sentence(),
                "type" to CompanyTypeEnum.RESTAURANT.toString(),
            )

        // Cria outra empresa
        val otherCompanyId =
            authenticatedRequest()
                .postWithAuth("/api/v1/company/companies", otherCompanyForm)
                .expectCreated()
                .extractLocationId()

        val otherTableForm =
            mapOf(
                "name" to "Mesa de Outra Empresa",
                "numPeople" to 4,
                "description" to "Mesa de teste para outra empresa",
                "companyId" to otherCompanyId.toString(),
            )

        // Cria uma mesa para a outra empresa
        val otherTableId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/tables", otherTableForm)
                .expectCreated()
                .extractLocationId()

        // Tenta trocar a comanda para a mesa da outra empresa
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/change-table", mapOf("newTableId" to otherTableId.toString()), commandId)
            .statusCode(400)
            .body("message", equalTo("A comanda e a mesa de destino devem pertencer à mesma empresa."))
    }

    @Test
    @Order(17)
    @DisplayName("Etapa 17: Garçom não deve ser capaz de trocar a comanda de mesa se a comanda não estiver aberta")
    fun `etapa 17 - garcom nao deve ser capaz de trocar a comanda de mesa se a comanda nao estiver aberta`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário para fechar a comanda

        // Troca o status da comanda para PAYING primeiro
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "PAYING"), commandId)
            .expectSuccess()

        // Troca o status da comanda para CLOSED
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "CLOSED"), commandId)
            .expectSuccess()

        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom para tentar mudar a mesa

        // Pega uma mesa diferente da atual
        val tables =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .getWithAuth("/api/v1/comandalivre/tables")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)

        // Pega o ID da mesa atual da comanda
        val currentCommandTableId =
            authenticatedRequest()
                .getWithAuth("/api/v1/comandalivre/commands/{id}", commandId)
                .expectSuccess()
                .extract()
                .path<String>("table.id")

        // Seleciona uma mesa diferente da atual
        val newTableId = UUID.fromString(tables.first { it["id"] as String != currentCommandTableId }["id"] as String)

        // Tenta trocar a comanda para a nova mesa
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/change-table", mapOf("newTableId" to newTableId.toString()), commandId)
            .statusCode(400)
            .body("message", equalTo("A comanda deve estar aberta para ter sua mesa alterada."))
    }

    @Test
    @Order(18)
    @DisplayName("Etapa 18: Proprietário não deve ser capaz de reabrir uma comanda já aberta")
    fun `etapa 18 - proprietario nao deve ser capaz de reabrir uma comanda ja aberta`() {
        setupJwt(ownerSub, ownerName, ownerEmail)

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "OPEN"), commandId)
            .expectSuccess()

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "OPEN"), commandId)
            .statusCode(400)
            .body("message", equalTo("Transição de status de 'open' para 'open' não é permitida."))
    }

    @Test
    @Order(19)
    @DisplayName("Etapa 19: Garçom deve ser capaz de adicionar mais produtos à comanda aberta")
    fun `etapa 19 - garcom deve ser capaz de adicionar mais produtos a comanda aberta`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom para adicionar produtos

        // Obtém a contagem inicial de pedidos
        val initialOrders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val initialOrderCount = initialOrders.size

        // Obtém produtos para adicionar
        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 2)
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productsToAdd = products.take(2)

        val orderItems =
            productsToAdd.map { product ->
                mapOf(
                    "productId" to (product["id"] as String),
                    "notes" to faker.lorem().sentence(5),
                )
            }

        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .expectCreated()

        // Verifica a contagem de pedidos atualizada
        val updatedOrders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        assertThat(updatedOrders.size).isGreaterThan(initialOrderCount)
    }

    @Test
    @Order(20)
    @DisplayName("Etapa 20: Garçom não deve ser capaz de adicionar produtos a uma comanda fechada")
    fun `etapa 20 - garcom nao deve ser capaz de adicionar produtos a uma comanda fechada`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário para fechar a comanda

        // Muda o status da comanda para PAYING
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "PAYING"), commandId)
            .expectSuccess()

        // Muda o status da comanda para CLOSED e fecha todas as ordens abertas
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "CLOSED", "closeAll" to true), commandId)
            .expectSuccess()

        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom para tentar adicionar produtos

        // Pega um produto para adicionar
        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 1)
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productToAdd = products.first()

        val orderItems =
            listOf(
                mapOf(
                    "productId" to (productToAdd["id"] as String),
                    "notes" to faker.lorem().sentence(5),
                ),
            )

        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        // Tenta adicionar o produto à comanda fechada
        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .statusCode(400)
            .body("message", equalTo("Não é possível adicionar um pedido para um comando fechado"))
    }

    @Test
    @Order(21)
    @DisplayName("Etapa 21: Garçom deve ser capaz de adicionar um produto com notas vazias à comanda aberta")
    fun `etapa 21 - garcom deve ser capaz de adicionar um produto com notas vazias a comanda aberta`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom para adicionar produtos

        // Garante que a comanda esteja aberta
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "OPEN"), commandId)
            .expectSuccess()

        val initialOrders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val initialOrderCount = initialOrders.size

        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 1)
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productToAdd = products.first()

        val orderItems =
            listOf(
                mapOf(
                    "productId" to (productToAdd["id"] as String),
                    "notes" to "", // Notas vazias
                ),
            )

        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .expectCreated()

        val updatedOrders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        assertThat(updatedOrders.size).isGreaterThan(initialOrderCount)
    }

    @Test
    @Order(22)
    @DisplayName("Etapa 22: Garçom não deve ser capaz de adicionar um produto inexistente à comanda")
    fun `etapa 22 - garcom nao deve ser capaz de adicionar um produto inexistente a comanda`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom para adicionar produtos

        val nonExistentProductPublicId = UUID.randomUUID()

        val orderItems =
            listOf(
                mapOf(
                    "productId" to nonExistentProductPublicId.toString(),
                    "notes" to faker.lorem().sentence(5),
                ),
            )

        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .statusCode(404)
            .body("message", equalTo("Produto não encontrado"))
    }

    @Test
    @Order(23)
    @DisplayName("Etapa 23: Garçom não deve ser capaz de adicionar um produto de outra empresa à comanda")
    fun `etapa 23 - garcom nao deve ser capaz de adicionar um produto de outra empresa a comanda`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom para adicionar produtos

        // Cria uma empresa e produto fictícios para outra empresa
        val otherCompanyForm =
            mapOf(
                "name" to faker.company().name(),
                "email" to faker.internet().emailAddress().replaceAfter("@", "comandalivre.com.br"),
                "phone" to faker.phoneNumber().cellPhone(),
                "cnpj" to null,
                "description" to faker.lorem().sentence(),
                "type" to CompanyTypeEnum.RESTAURANT.toString(),
            )

        val otherCompanyId =
            authenticatedRequest()
                .postWithAuth("/api/v1/company/companies", otherCompanyForm)
                .expectCreated()
                .extractLocationId()

        val otherProductCategory =
            authenticatedRequest()
                .getWithAuth("/api/v1/comandalivre/product-categories/list")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        val otherProductCategoryId = UUID.fromString(otherProductCategory.first { it["key"] == "appetizers" }["id"] as String)

        val otherProductForm =
            mapOf(
                "name" to faker.food().dish(),
                "price" to faker.number().randomDouble(2, 10, 200),
                "description" to faker.lorem().sentence(10),
                "ingredients" to listOf(faker.food().ingredient()),
                "servesPersons" to 1,
                "companyId" to otherCompanyId.toString(),
                "categoryId" to otherProductCategoryId.toString(),
                "availability" to true,
            )
        val otherProductId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/products", otherProductForm)
                .expectCreated()
                .extractLocationId()

        val orderItems =
            listOf(
                mapOf(
                    "productId" to otherProductId.toString(),
                    "notes" to faker.lorem().sentence(5),
                ),
            )

        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .statusCode(400)
            .body("message", notNullValue()) // A mensagem específica pode variar, mas deve ser uma exceção de lógica de negócio
    }

    @Test
    @Order(24)
    @DisplayName("Etapa 24: Deve retornar 401 ao obter a contagem de comandas sem permissão")
    fun `etapa 24 - deve retornar 401 ao obter a contagem de comandas sem permissao`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Requer permissão de ADMIN_DASHBOARD_ACCESS

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/count")
            .statusCode(401)
            .body(notNullValue())
    }

    @Test
    @Order(25)
    @DisplayName("Etapa 25: Deve obter os dados da conta para impressão")
    fun `etapa 25 - deve obter os dados da conta para impressao`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode acessar os dados da conta

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/commands/{commandId}/bill-data", commandId)
            .expectSuccess()
            .body("command", notNullValue())
            .body("company", notNullValue())
            .body("items", notNullValue())
    }

    @Test
    @Order(26)
    @DisplayName("Etapa 26: Deve obter um pedido por ID")
    fun `etapa 26 - deve obter um pedido por ID`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom pode acessar pedidos

        val orders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)

        assertThat(orders).isNotEmpty()
        val orderId = UUID.fromString(orders.first()["id"] as String)

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", orderId)
            .expectSuccess()
            .body("id", equalTo(orderId.toString()))
    }

    @Test
    @Order(27)
    @DisplayName("Etapa 27: Deve alterar o status de um pedido")
    fun `etapa 27 - deve alterar o status de um pedido`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom pode alterar status de pedidos

        val orders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)

        assertThat(orders).isNotEmpty()
        val orderId = UUID.fromString(orders.first()["id"] as String)

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/orders/{id}/status", mapOf("status" to "in_preparation"), orderId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", orderId)
            .expectSuccess()
            .body("status.key", equalTo("in_preparation"))
    }

    @Test
    @Order(28)
    @DisplayName("Etapa 28: Deve verificar se a comanda está totalmente fechada")
    fun `etapa 28 - deve verificar se a comanda esta totalmente fechada`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode verificar status da comanda

        // Muda da comanda para PAYING
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "PAYING"), commandId)
            .expectSuccess()

        // Fecha a comanda para garantir que todos os pedidos estejam fechados
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "CLOSED", "closeAll" to true), commandId)
            .expectSuccess()

        authenticatedRequest()
            .queryParam("commandId", commandId.toString())
            .getWithAuth("/api/v1/comandalivre/orders/is-command-fully-closed")
            .expectSuccess()
            .body(equalTo("true"))
    }

    @Test
    @Order(29)
    @DisplayName("Etapa 29: Deve remover um pedido")
    fun `etapa 29 - deve remover um pedido`() {
        setupJwt(inviteeSub, inviteeName, inviteeEmail) // Garçom pode remover pedidos

        // Cria um novo pedido para ser removido
        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 1)
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productToAdd = products.first()

        val orderItems = listOf(mapOf("productId" to (productToAdd["id"] as String), "notes" to "Pedido para remover"))
        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        // Muda status da comanda para OPEN
        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/commands/{id}/status", mapOf("status" to "OPEN"), commandId)
            .expectSuccess()

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .expectCreated()

        val orders =
            authenticatedRequest()
                .queryParam("commandId", commandId.toString())
                .getWithAuth("/api/v1/comandalivre/orders")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)

        val newOrderId = UUID.fromString(orders.filter({ it["notes"] == "Pedido para remover" }).first()["id"] as String)

        authenticatedRequest()
            .deleteWithAuth("/api/v1/comandalivre/orders/{id}", newOrderId.toString())
            .statusCode(204)

        // Verifica se o pedido foi realmente removido (espera-se 404 Not Found)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/orders/{id}", newOrderId)
            .statusCode(404)
    }

    @Test
    @Order(30)
    @DisplayName("Etapa 30: Deve atualizar um produto")
    fun `etapa 30 - deve atualizar um produto`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode atualizar produtos

        // Obtém um produto existente para atualizar
        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 1)
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productToUpdate = products.first()
        val productId = UUID.fromString(productToUpdate["id"] as String)

        val newName = faker.food().dish() + " Atualizado"
        val updateForm =
            mapOf(
                "name" to newName,
                "price" to faker.number().randomDouble(2, 10, 200),
                "description" to faker.lorem().sentence(),
                "companyId" to companyId.toString(),
                "categoryId" to (productToUpdate["category"] as Map<String, Any>)["id"].toString(),
                "availability" to true,
            )

        authenticatedRequest()
            .putWithAuth("/api/v1/comandalivre/products/{id}", updateForm, productId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/products/{id}", productId)
            .expectSuccess()
            .body("name", equalTo(newName))
    }

    @Test
    @Order(31)
    @DisplayName("Etapa 31: Deve alterar o status de disponibilidade de um produto")
    fun `etapa 31 - deve alterar o status de disponibilidade de um produto`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode alterar status de produtos

        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 1)
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val productToUpdate = products.first()
        val productId = UUID.fromString(productToUpdate["id"] as String)
        val currentAvailability = productToUpdate["availability"] as Boolean

        val newAvailability = !currentAvailability

        authenticatedRequest()
            .patchWithAuth("/api/v1/comandalivre/products/{id}/status/{status}", null, productId, newAvailability)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/products/{id}", productId)
            .expectSuccess()
            .body("availability", equalTo(newAvailability))
    }

    @Test
    @Order(32)
    @DisplayName("Etapa 32: Deve deletar um produto")
    fun `etapa 32 - deve deletar um produto`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode deletar produtos

        // Cria um novo produto para ser deletado
        val categories =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .getWithAuth("/api/v1/comandalivre/product-categories/list")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        val productCategoryId = UUID.fromString(categories[0]["id"] as String)

        val productForm =
            mapOf(
                "name" to faker.food().dish() + " para deletar",
                "price" to faker.number().randomDouble(2, 10, 200),
                "description" to faker.lorem().sentence(),
                "companyId" to companyId.toString(),
                "categoryId" to productCategoryId.toString(),
                "availability" to true,
            )

        val productIdToDelete =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/products", productForm)
                .expectCreated()
                .extractLocationId()

        authenticatedRequest()
            .deleteWithAuth("/api/v1/comandalivre/products/{id}", productIdToDelete)
            .statusCode(204)

        // Verifica se o produto foi realmente deletado (espera-se 404 Not Found)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/products/{id}", productIdToDelete)
            .statusCode(404)
    }

    @Test
    @Order(34)
    @DisplayName("Etapa 34: Deve obter a lista de mesas")
    fun `etapa 34 - deve obter a lista de mesas`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode acessar mesas

        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .getWithAuth("/api/v1/comandalivre/tables/list")
            .expectSuccess()
            .body("size()", greaterThan(0))
    }

    @Test
    @Order(35)
    @DisplayName("Etapa 35: Deve obter uma mesa por ID")
    fun `etapa 35 - deve obter uma mesa por ID`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode acessar mesas

        val tables =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .getWithAuth("/api/v1/comandalivre/tables/list")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)

        assertThat(tables).isNotEmpty()
        val tableIdToGet = UUID.fromString(tables.first()["id"] as String)

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", tableIdToGet)
            .expectSuccess()
            .body("id", equalTo(tableIdToGet.toString()))
    }

    @Test
    @Order(36)
    @DisplayName("Etapa 36: Deve criar mesas em lote")
    fun `etapa 36 - deve criar mesas em lote`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode criar mesas

        val bulkCreateForm =
            mapOf(
                "companyId" to companyId.toString(),
                "start" to 10,
                "end" to 99,
                "numPeople" to 4,
                "description" to "Mesas criadas em lote",
            )

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/tables/bulk", bulkCreateForm)
            .expectSuccess()

        // Verifica se as mesas foram criadas
        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .queryParam("search", "Mesa 100")
            .getWithAuth("/api/v1/comandalivre/tables")
            .expectSuccess()
            .body("content.size()", greaterThanOrEqualTo(1))
    }

    @Test
    @Order(37)
    @DisplayName("Etapa 37: Deve atualizar uma mesa")
    fun `etapa 37 - deve atualizar uma mesa`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode atualizar mesas

        val tables =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .queryParam("pageSize", 1)
                .getWithAuth("/api/v1/comandalivre/tables")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)
        val tableToUpdate = tables.first()
        val tableIdToUpdate = UUID.fromString(tableToUpdate["id"] as String)

        val newName = "Mesa Atualizada ${faker.number().digit()}"
        val updateForm =
            mapOf(
                "name" to newName,
                "numPeople" to 6,
                "description" to "Descrição atualizada da mesa",
            )

        authenticatedRequest()
            .putWithAuth("/api/v1/comandalivre/tables/{id}", updateForm, tableIdToUpdate)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", tableIdToUpdate)
            .expectSuccess()
            .body("name", equalTo(newName))
    }

    @Test
    @Order(38)
    @DisplayName("Etapa 38: Deve deletar uma mesa")
    fun `etapa 38 - deve deletar uma mesa`() {
        setupJwt(ownerSub, ownerName, ownerEmail) // Proprietário pode deletar mesas

        // Cria uma nova mesa para ser deletada
        val tableForm =
            mapOf(
                "name" to "Mesa para Deletar ${faker.number().digit()}",
                "numPeople" to 2,
                "description" to "Mesa temporária",
                "companyId" to companyId.toString(),
            )

        val tableIdToDelete =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/tables", tableForm)
                .expectCreated()
                .extractLocationId()

        authenticatedRequest()
            .deleteWithAuth("/api/v1/comandalivre/tables/{id}", tableIdToDelete)
            .statusCode(204)

        // Verifica se a mesa foi realmente deletada (espera-se 404 Not Found)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", tableIdToDelete)
            .statusCode(404)
    }

    @Test
    @Order(39)
    @DisplayName("Etapa 39: Fluxo - criar mesa, abrir comanda, adicionar pedidos, deletar mesa e verificar orders#getAll retorna 200")
    fun `etapa 39 - fluxo criar mesa abrir comanda adicionar pedidos deletar mesa verificar orders getAll retorna 200`() {
        // Cria uma nova mesa
        setupJwt(ownerSub, ownerName, ownerEmail)

        val tableForm =
            mapOf(
                "name" to "Mesa Fluxo ${faker.number().digit()}",
                "numPeople" to 4,
                "description" to "Mesa para fluxo de teste",
                "companyId" to companyId.toString(),
            )

        val newTableId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/tables", tableForm)
                .expectCreated()
                .extractLocationId()

        // Obtém o ID do funcionário (garçom) para criar a comanda
        val waiterEmployeeId =
            authenticatedRequest()
                .getWithAuth("/api/v1/company/employees/by-company/{companyId}", companyId)
                .expectSuccess()
                .extract()
                .path<String>("id")

        // Abre a comanda como garçom
        setupJwt(inviteeSub, inviteeName, inviteeEmail)
        val commandForm =
            mapOf(
                "name" to faker.name().fullName(),
                "numberOfPeople" to 2,
                "tableId" to newTableId.toString(),
                "employeeId" to waiterEmployeeId.toString(),
            )

        val newCommandId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/commands", commandForm)
                .expectCreated()
                .extractLocationId()

        // Adiciona pedidos à comanda
        // Busca um produto da empresa
        val products =
            authenticatedRequest()
                .queryParam("companyId", companyId.toString())
                .getWithAuth("/api/v1/comandalivre/products")
                .expectSuccess()
                .extract()
                .jsonPath()
                .getList("content", Map::class.java)

        val productId = UUID.fromString(products.first()["id"] as String)

        val orderItems = listOf(mapOf("productId" to productId.toString(), "notes" to "Pedido de fluxo"))
        val orderForm = mapOf("commandId" to newCommandId.toString(), "items" to orderItems)

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/orders", orderForm)
            .expectCreated()

        setupJwt(ownerSub, ownerName, ownerEmail)

        // Deleta a mesa
        authenticatedRequest()
            .deleteWithAuth("/api/v1/comandalivre/tables/{id}", newTableId)
            .statusCode(204)

        // Confirma que a mesa foi deletada
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", newTableId)
            .statusCode(404)

        // Verifica que o endpoint GET /orders (OrderController#getAll) retorna 200
        setupJwt(inviteeSub, inviteeName, inviteeEmail)
        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .getWithAuth("/api/v1/comandalivre/orders")
            .expectSuccess()
    }
}
