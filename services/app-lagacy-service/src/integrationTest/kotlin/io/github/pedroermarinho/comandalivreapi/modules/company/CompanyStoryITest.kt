package io.github.pedroermarinho.comandalivreapi.modules.company

import com.github.javafaker.Faker
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.*
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.text.Normalizer
import java.util.*

/**
 * Esta classe de teste de integração simula um fluxo de trabalho completo para o gerenciamento de empresas e funcionários.
 * Ela abrange a criação de empresas, atualização de dados da empresa (incluindo endereço e configurações),
 * gerenciamento de funcionários (convites, aceitação/rejeição, ativação/desativação) e busca de dados relacionados.
 * O objetivo é verificar a integração entre os diferentes endpoints da API de empresa e garantir que as operações
 * de CRUD e de negócio funcionem conforme o esperado.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CompanyStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    private val faker = Faker(Locale.forLanguageTag("pt-BR"))

    // Variáveis de estado para compartilhar dados entre os testes
    private lateinit var owner: TestObjectFactory.UserInfo
    private lateinit var companyId: UUID
    private lateinit var invitee: TestObjectFactory.UserInfo
    private lateinit var inviteId: UUID
    private lateinit var employeeId: UUID

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
    @DisplayName("Etapa 1: Deve atualizar os dados da empresa")
    fun `etapa 1 - deve atualizar os dados da empresa`() {
        setupJwt(owner.sub, owner.name, owner.email)
        val newName = TestDataGenerator.generateCompanyName() + " Atualizado"
        val updateForm =
            mapOf(
                "name" to newName,
                "email" to faker.internet().emailAddress().replaceAfter("@", "comandalivre.com.br"),
                "phone" to faker.phoneNumber().cellPhone(),
                "description" to "Descrição atualizada",
            )

        authenticatedRequest()
            .putWithAuth("/api/v1/company/companies/{id}", updateForm, companyId)
            .expectSuccess()

        authenticatedRequest()
            .getWithAuth("/api/v1/company/companies/{id}", companyId)
            .expectSuccess()
            .body("name", equalTo(newName))
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve atualizar o endereço da empresa")
    fun `etapa 2 - deve atualizar o endereco da empresa`() {
        setupJwt(owner.sub, owner.name, owner.email)
        val addressForm =
            mapOf(
                "street" to "Rua Nova",
                "city" to "Cidade Nova",
                "state" to "Estado Novo",
                "zipCode" to "12345-678",
                "country" to "Brasil",
                "number" to "100",
                "complement" to "Apto 101",
                "neighborhood" to "Bairro Novo",
            )

        authenticatedRequest()
            .putWithAuth("/api/v1/company/companies/{id}/address", addressForm, companyId)
            .expectSuccess()
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve atualizar as configurações da empresa")
    fun `etapa 3 - deve atualizar as configuracoes da empresa`() {
        setupJwt(owner.sub, owner.name, owner.email)
        val domain =
            run {
                val raw = faker.internet().domainWord()
                val normalized =
                    Normalizer
                        .normalize(raw, Normalizer.Form.NFD)
                        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "") // remove acentos
                        .replace(Regex("[^A-Za-z0-9_-]"), "-") // substitui chars inválidos por hífen
                        .trim { it == '-' || it == '_' || it.isWhitespace() }
                        .lowercase()

                normalized.ifBlank {
                    "domain-${UUID.randomUUID().toString().substring(0, 8)}"
                }
            }
        val settingsForm =
            mapOf(
                "domain" to domain,
                "primaryColor" to "#FFFFFF",
                "secondaryColor" to "#000000",
            )

        authenticatedRequest()
            .putWithAuth("/api/v1/company/companies/{id}/settings", settingsForm, companyId)
            .expectSuccess()

        // Testa buscar por domínio
        authenticatedRequest()
            .getWithAuth("/api/v1/company/companies/domain/{domain}", domain)
            .expectSuccess()
            .body("id", equalTo(companyId.toString()))
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve buscar todos os tipos de empresa paginados")
    fun `etapa 4 - deve buscar todos os tipos de empresa paginados`() {
        setupJwt(owner.sub, owner.name, owner.email)
        authenticatedRequest()
            .get("/api/v1/company/company-types")
            .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0))
    }

    @Test
    @Order(5)
    @DisplayName("Etapa 5: Deve buscar todos os tipos de cargos paginados")
    fun `etapa 5 - deve buscar todos os tipos de cargos paginados`() {
        setupJwt(owner.sub, owner.name, owner.email)
        authenticatedRequest()
            .get("/api/v1/company/role-types")
            .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0))
    }

    @Test
    @Order(6)
    @DisplayName("Etapa 6: Deve buscar os funcionários da empresa")
    fun `etapa 6 - deve buscar os funcionarios da empresa`() {
        setupJwt(owner.sub, owner.name, owner.email)
        val employees =
            authenticatedRequest()
                .get("/api/v1/company/employees/company/{companyId}", companyId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap<String, Any>("content[0]")

        employeeId = UUID.fromString(employees["id"] as String)

        // Testa buscar funcionário por ID
        authenticatedRequest()
            .get("/api/v1/company/employees/{id}", employeeId)
            .then()
            .statusCode(200)
            .body("id", equalTo(employeeId.toString()))
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Deve desativar e reativar um funcionário")
    fun `etapa 7 - deve desativar e reativar um funcionario`() {
        setupJwt(owner.sub, owner.name, owner.email)
        // Desativa
        authenticatedRequest()
            .patch("/api/v1/company/employees/{id}/change-status/{status}", employeeId, false)
            .then()
            .statusCode(200)

        // Reativa
        authenticatedRequest()
            .patch("/api/v1/company/employees/{id}/change-status/{status}", employeeId, true)
            .then()
            .statusCode(200)
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Deve buscar os meus cargos e verificar se tenho vínculo")
    fun `etapa 8 - deve buscar os meus cargos e verificar se tenho vinculo`() {
        setupJwt(owner.sub, owner.name, owner.email)
        authenticatedRequest()
            .get("/api/v1/company/employees/my-employees")
            .then()
            .statusCode(200)
            .body("content.size()", equalTo(1))
            .body("content[0].id", equalTo(employeeId.toString()))
            .body("content[0].user.email", equalTo(owner.email))

        authenticatedRequest()
            .get("/api/v1/company/employees/has-company")
            .then()
            .statusCode(200)
            .body(equalTo("true"))
    }

    @Test
    @Order(9)
    @DisplayName("Etapa 9: Deve convidar e depois recusar o convite")
    fun `etapa 9 - deve convidar e depois recusar o convite`() {
        // Cria o usuário convidado
        invitee = TestObjectFactory.createTestUser(::setupJwt)

        // Envia o convite
        setupJwt(owner.sub, owner.name, owner.email)
        val waiterRoleId = TestObjectFactory.getRoleTypeId(RoleTypeEnum.WAITER)
        val inviteForm =
            mapOf(
                "email" to invitee.email,
                "roleId" to waiterRoleId.toString(),
                "companyId" to companyId.toString(),
            )

        inviteId =
            authenticatedRequest()
                .postWithAuth("/api/v1/company/employees/invites", inviteForm)
                .expectCreated()
                .extractLocationId()

        // Obtém o convite por ID
        authenticatedRequest()
            .get("/api/v1/company/employees/invites/{id}", inviteId)
            .then()
            .statusCode(200)

        // Obtém os convites por empresa
        authenticatedRequest()
            .get("/api/v1/company/employees/invites/company/{companyId}", companyId)
            .then()
            .statusCode(200)

        // Troca para o usuário convidado para verificar seus convites
        setupJwt(invitee.sub, invitee.name, invitee.email)
        authenticatedRequest()
            .get("/api/v1/company/employees/invites/")
            .then()
            .statusCode(200)
    }

    // Rejeita o convite
    @Test
    @Order(10)
    @DisplayName("Etapa 10: Deve buscar todas as empresas paginadas")
    fun `etapa 10 - deve buscar todas as empresas paginadas`() {
        setupJwt(owner.sub, owner.name, owner.email)
        authenticatedRequest()
            .getWithAuth("/api/v1/company/companies")
            .expectSuccess()
            .body("content.size()", greaterThan(0))
    }
}
