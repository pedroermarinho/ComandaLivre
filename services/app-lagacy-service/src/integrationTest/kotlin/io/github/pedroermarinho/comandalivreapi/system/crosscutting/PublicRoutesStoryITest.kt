package io.github.pedroermarinho.comandalivreapi.system.crosscutting

import io.github.pedroermarinho.comandalivreapi.config.AbstractIntegrationTest
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Esta classe de teste de integração verifica o acesso a rotas públicas da API.
 * O objetivo é garantir que endpoints específicos, como listagem de empresas e produtos,
 * possam ser acessados sem autenticação, conforme o design da aplicação.
 */
class PublicRoutesStoryITest : AbstractIntegrationTest() {
    @Test
    @DisplayName("Deve permitir acesso a rotas públicas de empresas")
    fun `deve permitir acesso a rotas publicas de empresas`() {
        given()
            .`when`()
            .get("/api/v1/company/companies")
            .then()
            .statusCode(anyOf(`is`(200), `is`(404)))

        given()
            .`when`()
            .get("/api/v1/company/companies/{id}", UUID.randomUUID())
            .then()
            .statusCode(anyOf(`is`(200), `is`(404)))
    }

    @Test
    @DisplayName("Deve permitir acesso a rotas públicas de produtos")
    fun `deve permitir acesso a rotas publicas de produtos`() {
        given()
            .queryParam("companyId", UUID.randomUUID())
            .`when`()
            .get("/api/v1/comandalivre/products")
            .then()
            .statusCode(anyOf(`is`(200), `is`(404)))

        given()
            .`when`()
            .get("/api/v1/comandalivre/products/{id}", UUID.randomUUID())
            .then()
            .statusCode(anyOf(`is`(200), `is`(404)))
    }
}
