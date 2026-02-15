package io.github.pedroermarinho.comandalivreapi.system.crosscutting

import io.github.pedroermarinho.comandalivreapi.config.AuthIntegrationTest
import io.github.pedroermarinho.comandalivreapi.helpers.TestObjectFactory
import io.restassured.RestAssured.given
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.util.*

/**
 * Esta classe de teste de integração simula o fluxo de upload de arquivos para produtos e empresas.
 * Ela testa os endpoints de upload de imagem de produto, logo de empresa e banner de empresa,
 * verificando se os uploads são processados corretamente.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Disabled
class FileUploadStoryITest : AuthIntegrationTest() {
    @Autowired
    override lateinit var jwtDecoder: JwtDecoder

    // Variáveis de estado para compartilhar dados entre os testes
    private lateinit var owner: TestObjectFactory.UserInfo
    private lateinit var companyId: UUID
    private lateinit var productId: UUID

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

        val product = TestObjectFactory.createProduct(::setupJwt, owner, companyId)
        productId = product.id
    }

    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve fazer upload da imagem do produto")
    fun `etapa 1 - deve fazer upload da imagem do produto`() {
        setupJwt(owner.sub, owner.name, owner.email, featureFlag = true)
        val imageBytes = "conteúdo de imagem de teste".toByteArray()

        given()
            .header("Authorization", "Bearer mock-token")
            .multiPart("imageFile", "test_image.png", imageBytes, "image/png")
            .`when`()
            .patch("/api/v1/comandalivre/products/{id}/image", productId)
            .then()
            .statusCode(200)
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve fazer upload do logo da empresa")
    fun `etapa 2 - deve fazer upload do logo da empresa`() {
        setupJwt(owner.sub, owner.name, owner.email, featureFlag = true)
        val imageBytes = "conteúdo de logo de teste".toByteArray()

        given()
            .header("Authorization", "Bearer mock-token")
            .multiPart("imageFile", "test_logo.png", imageBytes, "image/png")
            .`when`()
            .patch("/api/v1/company/companies/{id}/logo", companyId)
            .then()
            .statusCode(200)
    }

    @Test
    @Order(3)
    @DisplayName("Etapa 3: Deve fazer upload do banner da empresa")
    fun `etapa 3 - deve fazer upload do banner da empresa`() {
        setupJwt(owner.sub, owner.name, owner.email, featureFlag = true)
        val imageBytes = "conteúdo de banner de teste".toByteArray()

        given()
            .header("Authorization", "Bearer mock-token")
            .multiPart("imageFile", "test_banner.png", imageBytes, "image/png")
            .`when`()
            .patch("/api/v1/company/companies/{id}/banner", companyId)
            .then()
            .statusCode(200)
    }
}
