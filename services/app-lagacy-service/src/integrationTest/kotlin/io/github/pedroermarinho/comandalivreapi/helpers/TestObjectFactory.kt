package io.github.pedroermarinho.comandalivreapi.helpers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.restassured.RestAssured.given
import java.util.*

object TestObjectFactory {
    data class UserInfo(
        val id: UUID,
        val sub: String,
        val name: String,
        val email: String,
    )

    data class CompanyInfo(
        val id: UUID,
        val typeId: UUID,
    )

    data class ProductInfo(
        val id: UUID,
        val categoryId: UUID,
    )

    data class TableInfo(
        val id: UUID,
    )

    data class EmployeeInviteInfo(
        val id: UUID,
    )

    fun createTestUser(setupJwt: (sub: String, name: String, email: String) -> Unit): UserInfo {
        val name = TestDataGenerator.generateName()
        val email = TestDataGenerator.generateEmail()
        val sub = TestDataGenerator.generateAuthSub()
        setupJwt(sub, name, email)

        val userId =
            authenticatedRequest()
                .postWithAuth("/api/v1/shared/users/auth", emptyMap<String, String>())
                .expectSuccess()
                .extractId()

        return UserInfo(userId, sub, name, email)
    }

    fun createCompany(
        setupJwt: (sub: String, name: String, email: String) -> Unit,
        owner: UserInfo,
        companyType: CompanyTypeEnum = CompanyTypeEnum.RESTAURANT,
    ): CompanyInfo {
        setupJwt(owner.sub, owner.name, owner.email)

        val companyTypeId = getCompanyTypeId(companyType)

        val companyForm =
            mapOf(
                "name" to TestDataGenerator.generateCompanyName(),
                "email" to TestDataGenerator.generateEmail(),
                "phone" to TestDataGenerator.generatePhone(),
                "cnpj" to TestDataGenerator.generateCnpj(),
                "description" to TestDataGenerator.generateDescription(),
                "type" to CompanyTypeEnum.RESTAURANT.toString(),
            )

        val companyId =
            authenticatedRequest()
                .postWithAuth("/api/v1/company/companies", companyForm)
                .expectCreated()
                .extractLocationId()

        return CompanyInfo(companyId, companyTypeId)
    }

    fun createProduct(
        setupJwt: (sub: String, name: String, email: String) -> Unit,
        owner: UserInfo,
        companyId: UUID,
    ): ProductInfo {
        setupJwt(owner.sub, owner.name, owner.email)

        val categoryId = getFirstProductCategoryId()

        val productForm =
            mapOf(
                "name" to TestDataGenerator.generateProductName(),
                "price" to TestDataGenerator.generatePrice(),
                "description" to TestDataGenerator.generateDescription(),
                "companyId" to companyId.toString(),
                "categoryId" to categoryId.toString(),
                "availability" to true,
            )

        val productId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/products", productForm)
                .expectCreated()
                .extractLocationId()

        return ProductInfo(productId, categoryId)
    }

    fun createTable(
        setupJwt: (sub: String, name: String, email: String) -> Unit,
        owner: UserInfo,
        companyId: UUID,
    ): TableInfo {
        setupJwt(owner.sub, owner.name, owner.email)

        val tableForm =
            mapOf(
                "name" to TestDataGenerator.generateTableName(),
                "numPeople" to TestDataGenerator.generatePeopleCount(),
                "description" to "Mesa com vista para ${TestDataGenerator.generateStreet()}",
                "companyId" to companyId.toString(),
            )

        val tableId =
            authenticatedRequest()
                .postWithAuth("/api/v1/comandalivre/tables", tableForm)
                .expectCreated()
                .extractLocationId()

        return TableInfo(tableId)
    }

    fun inviteEmployee(
        setupJwt: (sub: String, name: String, email: String) -> Unit,
        owner: UserInfo,
        companyId: UUID,
        role: RoleTypeEnum,
        inviteeEmail: String,
    ): EmployeeInviteInfo {
        setupJwt(owner.sub, owner.name, owner.email)

        val roleId = getRoleTypeId(role)

        val inviteForm =
            mapOf(
                "email" to inviteeEmail,
                "roleId" to roleId.toString(),
                "companyId" to companyId.toString(),
            )

        val inviteId =
            authenticatedRequest()
                .postWithAuth("/api/v1/company/employees/invites", inviteForm)
                .expectCreated()
                .extractLocationId()

        return EmployeeInviteInfo(inviteId)
    }

    fun acceptInvite(
        setupJwt: (sub: String, name: String, email: String) -> Unit,
        invitee: UserInfo,
        inviteId: UUID,
    ) {
        setupJwt(invitee.sub, invitee.name, invitee.email)

        authenticatedRequest()
            .patchWithAuth("/api/v1/company/employees/invites/{id}/accept", null, inviteId)
            .expectSuccess()
    }

    /**
     * Busca tipos de empresa e retorna o ID do tipo especificado
     */
    fun getCompanyTypeId(companyType: CompanyTypeEnum): UUID {
        val types =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/company/company-types/list")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        return UUID.fromString(types.first { it["key"] == companyType.value }["id"] as String)
    }

    /**
     * Busca tipos de papel e retorna o ID do tipo especificado
     */
    fun getRoleTypeId(role: RoleTypeEnum): UUID {
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
        return UUID.fromString(roles.first { it["key"] == role.value }["id"] as String)
    }

    /**
     * Busca categorias de produtos e retorna a primeira disponível
     */
    fun getFirstProductCategoryId(): UUID {
        val categories =
            given()
                .header("Authorization", "Bearer mock-token")
                .`when`()
                .get("/api/v1/comandalivre/product-categories/list")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", Map::class.java)
        return UUID.fromString(categories[0]["id"] as String)
    }
}
