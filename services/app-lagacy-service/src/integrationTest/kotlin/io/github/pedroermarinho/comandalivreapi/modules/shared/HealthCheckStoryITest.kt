package io.github.pedroermarinho.comandalivreapi.modules.shared

import io.github.pedroermarinho.comandalivreapi.config.AbstractIntegrationTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Esta classe de teste de integração verifica o endpoint de saúde da aplicação.
 * O objetivo é garantir que o serviço esteja respondendo corretamente e que seu status seja "UP".
 */
class HealthCheckStoryITest : AbstractIntegrationTest() {
    @Test
    @DisplayName("Deve retornar status UP para o endpoint de saúde")
    fun `deve retornar status UP para o endpoint de saude`() {
        given()
            .`when`()
            .get("/actuator/health")
            .then()
            .statusCode(200)
    }
}
