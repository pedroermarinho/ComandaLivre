package io.github.pedroermarinho.comandalivreapi.util

import java.util.UUID

object MockConstants {
    const val COMPANY_NAME = "Test Company"
    const val COMPANY_EMAIL = "test@company.com"
    const val COMPANY_PHONE_NUMBER = "11999999999"
    const val COMPANY_CNPJ = "29.952.180/0001-93"
    const val COMPANY_DESCRIPTION = "A test company for unit tests"
    const val COMPANY_TYPE_ID_INT = 1
    val COMPANY_TYPE_ID_UUID = UUID.randomUUID()
    const val COMPANY_ID_INT = 1
    val COMPANY_ID_UUID: UUID = UUID.randomUUID()

    const val USER_ID_INT = 1
    val USER_ID_UUID: UUID = UUID.randomUUID()
    const val USER_SUB = "auth0|1234567890"
    const val USER_NAME = "Test User"
    const val USER_EMAIL = "user@test.com"
    const val USER_PHONE = "11988888888"

    const val ROLE_TYPE_ID_INT = 1
    val ROLE_TYPE_ID_UUID: UUID = UUID.randomUUID()
    const val ROLE_TYPE_NAME = "Restaurant Owner"
    const val ROLE_TYPE_KEY = "RESTAURANT_OWNER"
}
