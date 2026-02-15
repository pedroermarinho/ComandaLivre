package io.github.pedroermarinho.comandalivreapi.modules.comandalivre

import com.github.javafaker.Faker
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
class TableStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    private val faker = Faker(Locale.forLanguageTag("pt-BR"))

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    private lateinit var ownerUser: TestObjectFactory.UserInfo
    private lateinit var companyId: UUID
    private lateinit var tableId: UUID

    @BeforeAll
    fun setupAll() {
        ownerUser = TestObjectFactory.createTestUser(::setupJwt)
        val company = TestObjectFactory.createCompany(::setupJwt, ownerUser)
        companyId = company.id
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve criar uma nova mesa")
    fun `etapa 1 - deve criar uma nova mesa`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val tableCreateRequest =
            mapOf(
                "name" to "Mesa ${faker.number().digit()}",
                "numPeople" to faker.number().numberBetween(2, 8),
                "description" to "Mesa com vista para ${faker.address().streetName()}",
                "companyId" to companyId.toString(),
            )

        tableId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/tables", tableCreateRequest)
                .expectCreated()
                .extractLocationId()
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve buscar todas as mesas paginadas")
    fun `etapa 2 - deve buscar todas as mesas paginadas`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .getWithAuth("/api/v1/comandalivre/tables")
            .expectSuccess()
            .body("content.size()", greaterThanOrEqualTo(1))
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve buscar todas as mesas em lista")
    fun `etapa 3 - deve buscar todas as mesas em lista`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .getWithAuth("/api/v1/comandalivre/tables/list")
            .expectSuccess()
            .body("size()", greaterThanOrEqualTo(1))
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve buscar mesa por ID")
    fun `etapa 4 - deve buscar mesa por ID`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", tableId)
            .expectSuccess()
            .body("id", equalTo(tableId.toString()))
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Deve criar mesas em lote")
    fun `etapa 5 - deve criar mesas em lote`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val tableBulkCreateRequest =
            mapOf(
                "companyId" to companyId.toString(),
                "start" to 10,
                "end" to 12,
                "numPeople" to 4,
                "description" to "Mesas do fundo",
            )

        authenticatedRequest()
            .postWithAuth("/api/v1/comandalivre/tables/bulk", tableBulkCreateRequest)
            .expectSuccess()

        authenticatedRequest()
            .queryParam("companyId", companyId.toString())
            .getWithAuth("/api/v1/comandalivre/tables")
            .expectSuccess()
            .body("content.size()", greaterThanOrEqualTo(4)) // Original table + 3 new tables
    }

    @Test
    @Order(6)
    @DisplayName("Etapa 6: Deve atualizar uma mesa existente")
    fun `etapa 6 - deve atualizar uma mesa existente`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        val tableUpdateForm =
            mapOf(
                "name" to "Mesa Atualizada",
                "numPeople" to 6,
                "description" to "Descrição atualizada",
            )

        authenticatedRequest()
            .putWithAuth("/api/v1/comandalivre/tables/{id}", tableUpdateForm, tableId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", tableId)
            .expectSuccess()
            .body("name", equalTo(tableUpdateForm["name"]))
            .body("numPeople", equalTo(tableUpdateForm["numPeople"]))
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Deve deletar uma mesa")
    fun `etapa 7 - deve deletar uma mesa`() {
        setupJwt(ownerUser.sub, ownerUser.name, ownerUser.email)
        authenticatedRequest()
            .deleteWithAuth("/api/v1/comandalivre/tables/{id}", tableId)

        authenticatedRequest()
            .getWithAuth("/api/v1/comandalivre/tables/{id}", tableId)
            .statusCode(404)
    }
}
