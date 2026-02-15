package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.Cnpj
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyName
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para CompanyEntity")
class CompanyEntityTest {
    @Test
    @DisplayName("Deve criar CompanyEntity com todas as propriedades")
    fun shouldCreateCompanyEntity() {
        val entityId = EntityId(internalId = 1, publicId = UUID.randomUUID())
        val audit =
            EntityAudit(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "test",
                updatedBy = "test",
                deletedAt = null,
                version = 0,
            )

        val companyType =
            CompanyType(
                id = EntityId(internalId = 1, publicId = UUID.randomUUID()),
                key = TypeKey("RETAIL"),
                name = TypeName("Varejo"),
                audit = audit,
            )

        val company =
            CompanyEntity(
                id = entityId,
                name = CompanyName("Test Company"),
                email = EmailAddress("test@company.com"),
                phone = PhoneNumber("11987654321"),
                cnpj = Cnpj("50.327.386/0001-19"),
                description = "A test company",
                companyType = companyType,
                addressId = AddressId(1),
                settings = CompanySettingsEntity.createNew(),
                isPublic = true,
                audit = audit,
            )

        assertEquals(entityId, company.id)
        assertEquals(CompanyName("Test Company"), company.name)
        assertEquals(EmailAddress("test@company.com"), company.email)
        assertEquals(PhoneNumber("11987654321"), company.phone)
        assertEquals(Cnpj("50.327.386/0001-19"), company.cnpj)
        assertEquals("A test company", company.description)
        assertEquals(companyType, company.companyType)
        assertEquals(AddressId(1), company.addressId)
        assertNotNull(company.settings)
        assertTrue(company.isPublic)
        assertEquals(audit, company.audit)
    }

    @Test
    @DisplayName("Deve criar nova CompanyEntity usando o método de fábrica createNew")
    fun shouldCreateNewCompanyEntityUsingFactoryMethod() {
        val companyType =
            CompanyType(
                id = EntityId(internalId = 1, publicId = UUID.randomUUID()),
                key = TypeKey("RETAIL"),
                name = TypeName("Varejo"),
                audit = EntityAudit.createNew(),
            )

        val company =
            CompanyEntity.createNew(
                name = "New Company",
                companyType = companyType,
                email = "new@company.com",
                phone = "11912345678",
                cnpj = "50.327.386/0001-19",
                description = "A new test company",
                addressId = null,
                isPublic = false,
            )

        assertNotNull(company.id.publicId)
        assertTrue(company.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(CompanyName("New Company"), company.name)
        assertEquals(companyType, company.companyType)
        assertEquals(EmailAddress("new@company.com"), company.email)
        assertEquals(PhoneNumber("11912345678"), company.phone)
        assertEquals(Cnpj("50.327.386/0001-19"), company.cnpj)
        assertEquals("A new test company", company.description)
        assertNull(company.addressId)
        assertFalse(company.isPublic)
        assertNotNull(company.audit.createdAt)
        assertTrue(company.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de CompanyEntity")
    fun shouldUpdateCompanyEntity() {
        val companyType =
            CompanyType(
                id = EntityId(internalId = 1, publicId = UUID.randomUUID()),
                key = TypeKey("RETAIL"),
                name = TypeName("Varejo"),
                audit = EntityAudit.createNew(),
            )

        val initialCompany =
            CompanyEntity.createNew(
                name = "Initial Company",
                companyType = companyType,
                email = "initial@company.com",
                phone = "11900000000",
                cnpj = "50.327.386/0001-19",
                description = "Initial description",
                addressId = null,
                isPublic = true,
            )

        val updatedCompany =
            initialCompany.update(
                name = "Updated Company",
                email = "updated@company.com",
                phone = "11999999999",
                cnpj = "50.327.386/0001-19",
                description = "Updated description",
                isPublic = false,
            )

        assertEquals(initialCompany.id, updatedCompany.id) // ID should remain the same
        assertEquals(CompanyName("Updated Company"), updatedCompany.name)
        assertEquals(EmailAddress("updated@company.com"), updatedCompany.email)
        assertEquals(PhoneNumber("11999999999"), updatedCompany.phone)
        assertEquals(Cnpj("50.327.386/0001-19"), updatedCompany.cnpj)
        assertEquals("Updated description", updatedCompany.description)
        assertFalse(updatedCompany.isPublic)
        assertNotEquals(initialCompany.audit.updatedAt, updatedCompany.audit.updatedAt) // updatedAt should change
    }
}
