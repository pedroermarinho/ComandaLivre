package io.github.pedroermarinho.comandalivreapi.modules.prumodigital

import com.github.javafaker.Faker
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.TestObjectFactory
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PrumoDigitalStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    private val faker = Faker(Locale.forLanguageTag("pt-BR"))

    private lateinit var ownerSub: String
    private lateinit var ownerEmail: String
    private lateinit var ownerName: String
    private lateinit var companyId: UUID
    private lateinit var projectId: UUID
    private lateinit var employeeSub: String
    private lateinit var employeeEmail: String
    private lateinit var employeeName: String
    private lateinit var employeeId: UUID
    private lateinit var employeeProjectAssignmentId: UUID
    private lateinit var dailyReportId: UUID
    private lateinit var dailyActivityId: UUID

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        @Primary
        fun jwtDecoder(): JwtDecoder = org.mockito.Mockito.mock(JwtDecoder::class.java)
    }

    @BeforeAll
    fun setupAll() {
        val owner = TestObjectFactory.createTestUser(::setupJwt)
        ownerSub = owner.sub
        ownerName = owner.name
        ownerEmail = owner.email

        val company = TestObjectFactory.createCompany(::setupJwt, owner, CompanyTypeEnum.CONSTRUCTION_COMPANY)
        companyId = company.id
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve criar um projeto")
    fun `etapa 1 - deve criar um projeto`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        val projectForm =
            mapOf(
                "name" to "Projeto " + faker.app().name(),
                "code" to faker.code().asin(),
                "companyId" to companyId.toString(),
            )

        val locationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(projectForm)
                .`when`()
                .post("/api/v1/prumodigital/projects")
                .then()
                .statusCode(201)
                .extract()
                .header("Location")
        projectId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1))

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/projects/{id}", projectId)
            .then()
            .statusCode(200)
            .body("name", equalTo(projectForm["name"]))
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve designar um funcionário ao projeto")
    fun `etapa 2 - deve designar um funcionario ao projeto`() {
        // Cria o usuário funcionário
        employeeName = faker.name().fullName()
        employeeEmail = faker.internet().emailAddress().replaceAfter("@", "comandalivre.com.br")
        employeeSub = "auth-sub-${UUID.randomUUID()}"
        setupJwt(employeeSub, employeeName, employeeEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .post("/api/v1/shared/users/auth")
            .then()
            .statusCode(200)

        // Proprietário convida o funcionário
        setupJwt(ownerSub, ownerName, ownerEmail)
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
        val roleId = UUID.fromString(roles.first { it["key"] == RoleTypeEnum.CIVIL_ENGINEER.value }["id"] as String)

        val inviteForm = mapOf("email" to employeeEmail, "roleId" to roleId.toString(), "companyId" to companyId.toString())
        val inviteLocationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(inviteForm)
                .`when`()
                .post("/api/v1/company/employees/invites")
                .then()
                .statusCode(201)
                .extract()
                .header("Location")
        val inviteId = UUID.fromString(inviteLocationHeader.substring(inviteLocationHeader.lastIndexOf('/') + 1))

        // Funcionário aceita o convite
        setupJwt(employeeSub, employeeName, employeeEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .patch("/api/v1/company/employees/invites/{id}/accept", inviteId)
            .then()
            .statusCode(200)

        // Proprietário busca o ID do funcionário
        setupJwt(ownerSub, ownerName, ownerEmail)
        val employees =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/company/employees/company/{companyId}", companyId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")
        employeeId = UUID.fromString(employees.first { (it["user"] as Map<String, Any>)["email"] == employeeEmail }["id"] as String)

        // Proprietário designa o funcionário ao projeto
        val assignmentForm = mapOf("employeeId" to employeeId.toString())
        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(assignmentForm)
            .`when`()
            .post("/api/v1/prumodigital/projects/{projectId}/team", projectId)
            .then()
            .statusCode(200)

        // Verifica se o funcionário está na equipe
        val assignments =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/projects/{projectId}/team", projectId)
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].employee.id", equalTo(employeeId.toString()))
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")
        employeeProjectAssignmentId =
            UUID.fromString(assignments.first { (it["employee"] as Map<String, Any>)["id"] == employeeId.toString() }["id"] as String)
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve criar um relatório diário")
    fun `etapa 3 - deve criar um relatorio diario`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        val weatherStatus =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/weather-status")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        val weatherStatusId = UUID.fromString(weatherStatus.first()["id"] as String)

        val dailyReportForm =
            mapOf(
                "projectId" to projectId.toString(),
                "reportDate" to
                    java.time.LocalDate
                        .now()
                        .toString(),
                "morningWeatherId" to weatherStatusId.toString(),
                "generalObservations" to faker.lorem().paragraph(),
            )

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(dailyReportForm)
            .`when`()
            .post("/api/v1/prumodigital/daily-reports")
            .then()
            .statusCode(200)

        val reports =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/daily-reports/project/{projectId}", projectId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")
        dailyReportId = UUID.fromString(reports.first()["id"] as String)
    }

    @Test
    @Order(4)
    @DisplayName("Etapa 4: Deve adicionar uma atividade ao relatório diário")
    fun `etapa 4 - deve adicionar uma atividade ao relatorio diario`() {
        setupJwt(ownerSub, ownerName, ownerEmail)

        val dailyActivityStatus =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/daily-activity-status")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        val dailyActivityStatusId = UUID.fromString(dailyActivityStatus.first()["id"] as String)

        val dailyActivityForm =
            mapOf(
                "dailyReportId" to dailyReportId.toString(),
                "description" to "Desenvolvimento da feature X",
                "statusId" to dailyActivityStatusId.toString(),
                "employeeId" to employeeProjectAssignmentId.toString(),
            )

        val locationHeader =
            given()
                .header("Authorization", "Bearer mock-token")
                .contentType(ContentType.JSON)
                .body(dailyActivityForm)
                .`when`()
                .post("/api/v1/prumodigital/daily-activities")
                .then()
                .statusCode(201)
                .extract()
                .header("Location")
        dailyActivityId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1))

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/daily-activities/{id}", dailyActivityId)
            .then()
            .statusCode(200)
            .body("activityDescription", equalTo("Desenvolvimento da feature X"))
    }

//    @Test
//    @Order(5)
//    @DisplayName("Etapa 5: Deve adicionar um anexo a uma atividade")
//    fun `etapa 5 - deve adicionar um anexo a uma atividade`() {
//        setupJwt(ownerSub, ownerName, ownerEmail)
//        val attachmentForm = mapOf(
//            "dailyActivityId" to dailyActivityId.toString(),
//            "url" to faker.internet().url(),
//            "description" to faker.lorem().sentence()
//        )
//
//        given()
//            .header("Authorization", "Bearer mock-token")
//            .contentType(ContentType.JSON)
//            .body(attachmentForm)
//            .`when`()
//            .post("/api/v1/prumodigital/activity-attachments")
//            .then()
//            .statusCode(200)
//    }

    @Test
    @Order(6)
    @DisplayName("Etapa 6: Deve registrar presença no relatório diário")
    fun `etapa 6 - deve registrar presenca no relatorio diario`() {
        setupJwt(employeeSub, employeeName, employeeEmail)
        val attendanceForm =
            mapOf(
                "dailyReportId" to dailyReportId.toString(),
                "employeeId" to employeeId.toString(),
                "present" to true,
            )

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(attendanceForm)
            .`when`()
            .post("/api/v1/prumodigital/daily-attendances")
            .then()
            .statusCode(200)

        given()
            .header("Authorization", "Bearer mock-token")
            .queryParam("dailyReportId", dailyReportId)
            .`when`()
            .get("/api/v1/prumodigital/daily-attendances")
            .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0))
            .body("content[0].present", equalTo(true))
    }

    @Test
    @Order(7)
    @DisplayName("Etapa 7: Deve buscar o dashboard do PrumoDigital")
    fun `etapa 7 - deve buscar o dashboard do prumodigital`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/dashboard/projects-by-status")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
    }

    @Test
    @Order(8)
    @DisplayName("Etapa 8: Deve buscar os status de clima")
    fun `etapa 8 - deve buscar os status de clima`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/weather-status")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
    }

//    @Test
//    @Order(10)
//    @DisplayName("Etapa 10: Deve registrar minha atividade diária")
//    fun `etapa 10 - deve registrar minha atividade diaria`() {
//        setupJwt(employeeSub, employeeName, employeeEmail)
//        val myActivityRequest = mapOf(
//            "description" to "Minha atividade do dia",
//            "projectId" to projectId.toString(),
//            "statusKey" to DailyActivityStatus
//        )
//
//        given()
//            .header("Authorization", "Bearer mock-token")
//            .contentType(ContentType.JSON)
//            .body(myActivityRequest)
//            .`when`()
//            .post("/api/v1/prumodigital/daily-activities/my-activity")
//            .then()
//            .statusCode(200)
//    }

    @Test
    @Order(11)
    @DisplayName("Etapa 11: Deve buscar todas as atividades diárias")
    fun `etapa 11 - deve buscar todas as atividades diarias`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .queryParam("dailyReportId", dailyReportId)
            .`when`()
            .get("/api/v1/prumodigital/daily-activities")
            .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0))
    }

    @Test
    @Order(12)
    @DisplayName("Etapa 12: Deve atualizar uma atividade diária")
    fun `etapa 12 - deve atualizar uma atividade diaria`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        val dailyActivityStatus =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/daily-activity-status")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        val dailyActivityStatusId = UUID.fromString(dailyActivityStatus.first()["id"] as String)

        val updateForm =
            mapOf(
                "dailyReportId" to dailyReportId.toString(),
                "description" to "Descrição da atividade atualizada",
                "statusId" to dailyActivityStatusId.toString(),
                "employeeId" to employeeProjectAssignmentId.toString(),
            )

        given()
            .header("Authorization", "Bearer mock-token")
            .contentType(ContentType.JSON)
            .body(updateForm)
            .`when`()
            .put("/api/v1/prumodigital/daily-activities/{id}", dailyActivityId)
            .then()
            .statusCode(200)

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/daily-activities/{id}", dailyActivityId)
            .then()
            .statusCode(200)
            .body("activityDescription", equalTo("Descrição da atividade atualizada"))
    }

    @Test
    @Order(14)
    @DisplayName("Etapa 14: Deve buscar presença por ID público")
    fun `etapa 14 - deve buscar presenca por ID publico`() {
        setupJwt(employeeSub, employeeName, employeeEmail)
        val attendanceForm =
            mapOf(
                "dailyReportId" to dailyReportId.toString(),
                "employeeId" to employeeId.toString(),
                "present" to true,
            )

        val attendanceId =
            given()
                .header("Authorization", "Bearer mock-token")
                .queryParam("dailyReportId", dailyReportId)
                .`when`()
                .get("/api/v1/prumodigital/daily-attendances")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>("content")[0]["id"]

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/daily-attendances/{id}", attendanceId)
            .then()
            .statusCode(200)
            .body("id", equalTo(attendanceId))
    }

    @Test
    @Order(17)
    @DisplayName("Etapa 17: Deve buscar todos os projetos paginados")
    fun `etapa 17 - deve buscar todos os projetos paginados`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        given()
            .header("Authorization", "Bearer mock-token")
            .queryParam("companyId", companyId)
            .`when`()
            .get("/api/v1/prumodigital/projects")
            .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0))
    }

    @Test
    @Order(19)
    @DisplayName("Etapa 19: Deve buscar status de projeto por ID")
    fun `etapa 19 - deve buscar status de projeto por ID`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        val projectStatusId =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/project-status")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>(".")[0]["id"]

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/project-status/{id}", projectStatusId)
            .then()
            .statusCode(200)
            .body("id", equalTo(projectStatusId))
    }

    @Test
    @Order(20)
    @DisplayName("Etapa 20: Deve buscar status de projeto por chave")
    fun `etapa 20 - deve buscar status de projeto por chave`() {
        setupJwt(ownerSub, ownerName, ownerEmail)
        val projectStatusKey =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/prumodigital/project-status")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList<Map<String, Any>>(".")[0]["key"]

        given()
            .header("Authorization", "Bearer mock-token")
            .`when`()
            .get("/api/v1/prumodigital/project-status/key/{key}", projectStatusKey)
            .then()
            .statusCode(200)
            .body("key", equalTo(projectStatusKey))
    }
}
