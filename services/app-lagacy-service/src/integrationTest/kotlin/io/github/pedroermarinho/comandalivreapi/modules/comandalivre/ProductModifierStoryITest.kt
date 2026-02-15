package io.github.pedroermarinho.comandalivreapi.modules.comandalivre

import com.github.javafaker.Faker
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.TestObjectFactory
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
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
 * Esta classe de teste de integração simula um fluxo de trabalho para o gerenciamento de produtos com modificadores.
 * Ela abrange a criação de produtos, grupos de modificadores e opções de modificadores,
 * além de testar a adição de produtos com modificadores a uma comanda e a validação de regras de seleção de modificadores.
 * O objetivo é garantir que a funcionalidade de modificadores de produto funcione corretamente em um cenário de ponta a ponta.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProductModifierStoryITest : AuthIntegrationTest() {
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
    private lateinit var productId: UUID
    private lateinit var modifierGroupId: UUID
    private lateinit var modifierOptionId: UUID
    private lateinit var tableId: UUID
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
        productId = product.id
        productCategoryId = product.categoryId
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve criar um grupo de modificadores para o produto")
    fun `etapa 4 - deve criar um grupo de modificadores para o produto`() {
        val modifierGroupForm =
            mapOf(
                "name" to "Ponto da Carne",
                "minSelection" to 1,
                "maxSelection" to 3,
                "displayOrder" to 1,
            )

        val locationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(modifierGroupForm)
                .`when`()
                .post("/api/v1/comandalivre/products/{productId}/modifier-groups", productId)
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract()
                .header("Location")

        modifierGroupId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1))
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Deve criar opções de modificadores para o grupo")
    fun `etapa 5 - deve criar opcoes de modificadores para o grupo`() {
        val modifierOptionForm1 =
            mapOf(
                "name" to "Mal Passado",
                "priceChange" to 0.00,
                "isDefault" to false,
                "displayOrder" to 1,
            )

        val locationHeader1 =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(modifierOptionForm1)
                .`when`()
                .post("/api/v1/comandalivre/products/modifier-groups/{groupId}/options", modifierGroupId)
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract()
                .header("Location")

        modifierOptionId = UUID.fromString(locationHeader1.substring(locationHeader1.lastIndexOf('/') + 1))

        val modifierOptionForm2 =
            mapOf(
                "name" to "Ao Ponto",
                "priceChange" to 0.00,
                "isDefault" to true,
                "displayOrder" to 2,
            )

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(modifierOptionForm2)
            .`when`()
            .post("/api/v1/comandalivre/products/modifier-groups/{groupId}/options", modifierGroupId)
            .then()
            .statusCode(201)
            .header("Location", notNullValue())

        val modifierOptionForm3 =
            mapOf(
                "name" to "Bem Passado",
                "priceChange" to 0.00,
                "isDefault" to false,
                "displayOrder" to 3,
            )

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(modifierOptionForm3)
            .`when`()
            .post("/api/v1/comandalivre/products/modifier-groups/{groupId}/options", modifierGroupId)
            .then()
            .statusCode(201)
            .header("Location", notNullValue())
    }

    @Test
    @Order(6)
    @DisplayName("Etapa 6: Deve obter o produto com seus modificadores")
    fun `etapa 6 - deve obter o produto com seus modificadores`() {
        val product =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/comandalivre/products/{productId}", productId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get<Map<String, Any>>(".")

        assertThat(product["modifierGroups"]).isNotNull()
        val modifierGroups = product["modifierGroups"] as List<Map<String, Any>>
        assertThat(modifierGroups).hasSize(1)
        assertThat(modifierGroups[0]["name"]).isEqualTo("Ponto da Carne")
        assertThat(modifierGroups[0]["options"]).isNotNull()
        val options = modifierGroups[0]["options"] as List<Map<String, Any>>
        assertThat(options).hasSize(3)
        assertThat(options.any { it["name"] == "Mal Passado" }).isTrue()
        assertThat(options.any { it["name"] == "Ao Ponto" && it["isDefault"] == true }).isTrue()
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Deve adicionar uma mesa ao restaurante")
    fun `etapa 7 - deve adicionar uma mesa ao restaurante`() {
        val tableForm =
            mapOf(
                "name" to "Mesa ${faker.number().digit()}",
                "numPeople" to faker.number().numberBetween(2, 8),
                "description" to "Mesa com vista para ${faker.address().streetName()}",
                "companyId" to companyId.toString(),
            )

        val locationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(tableForm)
                .`when`()
                .post("/api/v1/comandalivre/tables")
                .then()
                .statusCode(201)
                .extract()
                .header("Location")

        tableId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1))
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Garçom deve criar uma nova comanda para uma mesa")
    fun `etapa 8 - garcom deve criar uma nova comanda para uma mesa`() {
        // Cria um usuário garçom
        val inviteeName = faker.name().fullName()
        val inviteeEmail = faker.internet().emailAddress().replaceAfter("@", "comandalivre.com.br")
        val inviteeSub = "auth-sub-${UUID.randomUUID()}"
        setupJwt(inviteeSub, inviteeName, inviteeEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .post("/api/v1/shared/users/auth")
            .then()
            .statusCode(200)

        // Obtém o ID do cargo de GARÇOM
        val roles =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/company/role-types/list")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        val waiterRoleId = UUID.fromString(roles.first { it["key"] == RoleTypeEnum.WAITER.value }["id"] as String)

        // Volta para o proprietário para enviar o convite
        setupJwt(ownerSub, ownerName, ownerEmail)
        val inviteForm =
            mapOf(
                "email" to inviteeEmail,
                "roleId" to waiterRoleId.toString(),
                "companyId" to companyId.toString(),
            )

        val inviteLocationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(inviteForm)
                .`when`()
                .post("/api/v1/company/employees/invites")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract()
                .header("Location")

        val inviteId = UUID.fromString(inviteLocationHeader.substring(inviteLocationHeader.lastIndexOf('/') + 1))

        // Muda para o usuário convidado para aceitar o convite
        setupJwt(inviteeSub, inviteeName, inviteeEmail)

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .patch("/api/v1/company/employees/invites/{id}/accept", inviteId)
            .then()
            .statusCode(200)

        // Encontra o ID do funcionário para o garçom
        setupJwt(ownerSub, ownerName, ownerEmail) // Loga como proprietário para ver os funcionários
        val waiterEmployeeId =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/company/employees/by-company/{companyId}", companyId)
                .then()
                .statusCode(200)
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

        val locationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(commandForm)
                .`when`()
                .post("/api/v1/comandalivre/commands")
                .then()
                .statusCode(201)
                .extract()
                .header("Location")

        commandId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1))
    }

    @Test
    @Order(9)
    @DisplayName("Etapa 9: Deve adicionar um produto com modificador à comanda")
    fun `etapa 9 - deve adicionar um produto com modificador a comanda`() {
        // Obtém o produto com modificadores
        val product =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/comandalivre/products/{productId}", productId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get<Map<String, Any>>(".")

        val modifierGroups = product["modifierGroups"] as List<Map<String, Any>>
        val options = modifierGroups[0]["options"] as List<Map<String, Any>>

        // Seleciona uma opção (ex: "Ao Ponto")
        val selectedOptionId = options.first { it["name"] == "Ao Ponto" }["id"] as String

        val orderItems =
            listOf(
                mapOf(
                    "productId" to productId.toString(),
                    "notes" to "Com modificador",
                    "selectedModifierOptionIds" to listOf(selectedOptionId),
                ),
            )
        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(orderForm)
            .`when`()
            .post("/api/v1/comandalivre/orders")
            .then()
            .statusCode(201)
    }

    @Test
    @Order(10)
    @DisplayName("Etapa 10: Não deve adicionar um produto com modificadores inválidos (min_selection)")
    fun `etapa 10 - nao deve adicionar um produto com modificadores invalidos min_selection`() {
        // Obtém o produto com modificadores
        val product =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/comandalivre/products/{productId}", productId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get<Map<String, Any>>(".")

        val modifierGroups = product["modifierGroups"] as List<Map<String, Any>>
        val options = modifierGroups[0]["options"] as List<Map<String, Any>>

        // Tenta adicionar sem selecionar nenhuma opção (min_selection = 1)
        val orderItems =
            listOf(
                mapOf(
                    "productId" to productId.toString(),
                    "notes" to "Sem modificador",
                    "selectedModifierOptionIds" to emptyList<String>(),
                ),
            )
        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(orderForm)
            .`when`()
            .post("/api/v1/comandalivre/orders")
            .then()
            .statusCode(400)
            .body("message", equalTo("O grupo 'Ponto da Carne' requer no mínimo 1 seleções."))
    }
//
//    @Test
//    @Order(11)
//    @DisplayName("Etapa 11: Não deve adicionar um produto com modificadores inválidos (max_selection)")
//    fun `etapa 11 - nao deve adicionar um produto com modificadores invalidos max_selection`() {
//        // Obtém o produto com modificadores
//        val product = given()
//            .header("Authorization", "Bearer mock-token")
//            .`when`()
//            .get("/api/v1/comandalivre/products/{productId}", productId)
//            .then()
//            .statusCode(200)
//            .extract()
//            .jsonPath()
//            .get<Map<String, Any>>(".")
//
//        val modifierGroups = product["modifierGroups"] as List<Map<String, Any>>
//        val options = modifierGroups[0]["options"] as List<Map<String, Any>>
//
//        // Seleciona mais de uma opção (max_selection = 1)
//        val selectedOptionId1 = options.first { it["name"] == "Mal Passado" }["id"] as String
//        val selectedOptionId2 = options.first { it["name"] == "Ao Ponto" }["id"] as String
//
//        val orderItems = listOf(mapOf(
//            "productId" to productId.toString(),
//            "notes" to "Com modificadores demais",
//            "selectedModifierOptionIds" to listOf(selectedOptionId1, selectedOptionId2)
//        ))
//        val orderForm = mapOf("commandId" to commandId.toString(), "items" to orderItems)
//
//        given()
//            .header("Authorization", "Bearer mock-token")
//            .contentType(ContentType.JSON)
//            .body(orderForm)
//            .`when`()
//            .post("/api/v1/comandalivre/orders")
//            .then()
//            .statusCode(400)
//            .body("message", equalTo("O grupo 'Ponto da Carne' permite no máximo 1 seleções."))
//    }
}
