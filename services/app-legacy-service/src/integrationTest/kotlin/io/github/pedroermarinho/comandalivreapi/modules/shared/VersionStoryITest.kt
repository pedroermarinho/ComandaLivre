package io.github.pedroermarinho.comandalivreapi.modules.shared

import io.github.pedroermarinho.comandalivreapi.config.AbstractIntegrationTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*

/**
 * Esta classe de teste de integração verifica a funcionalidade de gerenciamento de versões da aplicação.
 * Ela testa a criação de novas versões e a recuperação da última versão disponível para uma plataforma específica.
 * O objetivo é garantir que o controle de versão da aplicação funcione corretamente.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class VersionStoryITest : AbstractIntegrationTest() {
    @Test
    @Order(1)
    @DisplayName("Etapa 1: Deve criar uma nova versão")
    fun `etapa 1 - deve criar uma nova versao`() {
        val versionForm =
            mapOf(
                "version" to "1.0.0",
                "platform" to "ANDROID",
            )

        given()
            .contentType(ContentType.JSON)
            .body(versionForm)
            .`when`()
            .post("/api/v1/shared/app-versions")
            .then()
            .statusCode(201)
    }

    @Test
    @Order(2)
    @DisplayName("Etapa 2: Deve buscar a última versão")
    fun `etapa 2 - deve buscar a ultima versao`() {
        given()
            .`when`()
            .get("/api/v1/shared/app-versions/ANDROID")
            .then()
            .statusCode(200)
            .body("version", equalTo("1.0.0"))
            .body("platform", equalTo("android"))
    }
}
