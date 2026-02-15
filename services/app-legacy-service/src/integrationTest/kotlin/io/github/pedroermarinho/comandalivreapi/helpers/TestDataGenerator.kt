package io.github.pedroermarinho.comandalivreapi.helpers

import com.github.javafaker.Faker
import java.util.*

/**
 * Gerador de dados de teste usando Faker
 * Centraliza a geração de dados aleatórios consistentes
 */
object TestDataGenerator {
    private val faker = Faker(Locale.forLanguageTag("pt-BR"))

    fun generateName(): String = faker.name().fullName()

    fun generateEmail(domain: String = "comandalivre.com.br"): String = faker.internet().emailAddress().replaceAfter("@", domain)

    fun generatePhone(): String = faker.phoneNumber().cellPhone()

    fun generateCnpj(): String? = null

    fun generateCompanyName(): String = faker.company().name()

    fun generateDescription(): String = faker.lorem().sentence()

    fun generateProductName(): String = faker.food().dish()

    fun generatePrice(
        min: Long = 10,
        max: Long = 200,
    ): Double = faker.number().randomDouble(2, min, max)

    fun generateTableName(): String = "Mesa ${faker.number().digit()}${faker.number().digit()}${faker.number().digit()}"

    fun generatePeopleCount(
        min: Int = 2,
        max: Int = 8,
    ): Int = faker.number().numberBetween(min, max)

    fun generateStreet(): String = faker.address().streetName()

    fun generateAuthSub(): String = "auth-sub-${UUID.randomUUID()}"
}
