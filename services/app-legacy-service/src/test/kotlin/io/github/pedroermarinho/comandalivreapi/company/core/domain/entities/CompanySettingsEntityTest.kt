package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.DomainName
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para CompanySettingsEntity")
class CompanySettingsEntityTest {
    @Test
    @DisplayName("Deve criar CompanySettingsEntity com todas as propriedades")
    fun shouldCreateCompanySettingsEntity() {
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
        val settings =
            CompanySettingsEntity(
                id = entityId,
                companyId = CompanyId.restore(1),
                logoAssetId = AssetId(1),
                bannerAssetId = null,
                primaryThemeColor = "#FFFFFF",
                secondaryThemeColor = "#000000",
                welcomeMessage = "Welcome!",
                timezone = "America/Sao_Paulo",
                openTime = LocalTime.of(9, 0),
                closeTime = LocalTime.of(18, 0),
                isClosed = false,
                notificationEmails = listOf(EmailAddress("notify@example.com")),
                domain = DomainName("example"),
                audit = audit,
            )

        assertEquals(entityId, settings.id)
        assertEquals(CompanyId.restore(1), settings.companyId)
        assertEquals(AssetId(1), settings.logoAssetId)
        assertNull(settings.bannerAssetId)
        assertEquals("#FFFFFF", settings.primaryThemeColor)
        assertEquals("#000000", settings.secondaryThemeColor)
        assertEquals("Welcome!", settings.welcomeMessage)
        assertEquals("America/Sao_Paulo", settings.timezone)
        assertEquals(LocalTime.of(9, 0), settings.openTime)
        assertEquals(LocalTime.of(18, 0), settings.closeTime)
        assertFalse(settings.isClosed!!)
        assertEquals(listOf(EmailAddress("notify@example.com")), settings.notificationEmails)
        assertEquals(DomainName("example"), settings.domain)
        assertEquals(audit, settings.audit)
    }

    @Test
    @DisplayName("Deve criar nova CompanySettingsEntity usando o método de fábrica createNew")
    fun shouldCreateNewCompanySettingsEntityUsingFactoryMethod() {
        val settings =
            CompanySettingsEntity.createNew(
                companyId = CompanyId.restore(2),
                logoAssetId = 2,
                primaryThemeColor = "#FF0000",
                welcomeMessage = "Hello!",
                openTime = LocalTime.of(8, 0),
                closeTime = LocalTime.of(17, 0),
                isClosed = true,
                notificationEmails = listOf("admin@example.com"),
                domain = "testdomain",
            )

        assertNotNull(settings.id.publicId)
        assertTrue(settings.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(CompanyId.restore(2), settings.companyId)
        assertEquals(AssetId(2), settings.logoAssetId)
        assertEquals("#FF0000", settings.primaryThemeColor)
        assertEquals("Hello!", settings.welcomeMessage)
        assertEquals(LocalTime.of(8, 0), settings.openTime)
        assertEquals(LocalTime.of(17, 0), settings.closeTime)
        assertTrue(settings.isClosed!!)
        assertEquals(listOf(EmailAddress("admin@example.com")), settings.notificationEmails)
        assertEquals(DomainName("testdomain"), settings.domain)
        assertNotNull(settings.audit.createdAt)
        assertTrue(settings.isNew())
    }
}
