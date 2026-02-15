package io.github.pedroermarinho.comandalivreapi.helpers

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import java.util.*

/**
 * Extension functions para simplificar chamadas REST com autenticação
 */

fun authenticatedRequest(): RequestSpecification = given().header("Authorization", "Bearer mock-token")

fun RequestSpecification.getWithAuth(
    path: String,
    vararg pathParams: Any,
): ValidatableResponse =
    this
        .header("Authorization", "Bearer mock-token")
        .`when`()
        .get(path, *pathParams)
        .then()

fun RequestSpecification.postWithAuth(
    path: String,
    body: Any,
): ValidatableResponse =
    this
        .header("Authorization", "Bearer mock-token")
        .contentType(ContentType.JSON)
        .body(body)
        .`when`()
        .post(path)
        .then()

fun RequestSpecification.putWithAuth(
    path: String,
    body: Any,
    vararg pathParams: Any,
): ValidatableResponse =
    this
        .header("Authorization", "Bearer mock-token")
        .contentType(ContentType.JSON)
        .body(body)
        .`when`()
        .put(path, *pathParams)
        .then()

fun RequestSpecification.patchWithAuth(
    path: String,
    body: Any? = null,
    vararg pathParams: Any,
): ValidatableResponse {
    val spec = this.header("Authorization", "Bearer mock-token")
    if (body != null) {
        spec.contentType(ContentType.JSON).body(body)
    }
    return spec
        .`when`()
        .patch(path, *pathParams)
        .then()
}

fun RequestSpecification.deleteWithAuth(
    path: String,
    vararg pathParams: Any,
): ValidatableResponse =
    this
        .header("Authorization", "Bearer mock-token")
        .`when`()
        .delete(path, *pathParams)
        .then()

fun ValidatableResponse.extractId(): UUID = UUID.fromString(this.extract().path<String>("id"))

fun ValidatableResponse.extractLocationId(): UUID {
    val location = this.extract().header("Location")
    return UUID.fromString(location.substring(location.lastIndexOf('/') + 1))
}

fun ValidatableResponse.expectSuccess(statusCode: Int = 200): ValidatableResponse = this.statusCode(statusCode)

fun ValidatableResponse.expectCreated(): ValidatableResponse = this.statusCode(201)
