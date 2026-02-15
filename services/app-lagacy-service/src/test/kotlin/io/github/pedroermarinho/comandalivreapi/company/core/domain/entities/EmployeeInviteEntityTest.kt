package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.EmployeeInviteStatus
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EmailAddress
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para EmployeeInviteEntity")
class EmployeeInviteEntityTest {
    @Test
    @DisplayName("Deve criar EmployeeInviteEntity com todas as propriedades")
    fun shouldCreateEmployeeInviteEntity() {
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
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("RESTAURANT"),
                name = TypeName("Restaurant"),
                audit = audit,
            )
        val role =
            RoleType(
                id = entityId,
                key = TypeKey.restore("ADMIN"),
                name = TypeName.restore("Administrator"),
                description = "Administrator role",
                audit = audit,
                companyType = companyType,
            )

        val status =
            EmployeeInviteStatus(
                id = entityId,
                key = TypeKey.restore("ADMIN"),
                name = TypeName.restore("Administrator"),
                audit = audit,
            )

        val invite =
            EmployeeInviteEntity(
                id = entityId,
                token = UUID.randomUUID(),
                expirationDate = LocalDate.now().plusDays(7),
                email = EmailAddress("invite@example.com"),
                userId = null,
                companyId = CompanyId(1),
                status = status,
                role = role,
                audit = audit,
            )

        assertEquals(entityId, invite.id)
        assertNotNull(invite.token)
        assertEquals(LocalDate.now().plusDays(7), invite.expirationDate)
        assertEquals(EmailAddress("invite@example.com"), invite.email)
        assertNull(invite.userId)
        assertEquals(CompanyId(1), invite.companyId)
        assertEquals(status, invite.status)
        assertEquals(role, invite.role)
        assertEquals(audit, invite.audit)
    }

    @Test
    @DisplayName("Deve criar nova EmployeeInviteEntity usando o método de fábrica createNew")
    fun shouldCreateNewEmployeeInviteEntityUsingFactoryMethod() {
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
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("RESTAURANT"),
                name = TypeName("Restaurant"),
                audit = audit,
            )
        val role =
            RoleType(
                id = entityId,
                key = TypeKey.restore("ADMIN"),
                name = TypeName.restore("Administrator"),
                description = "Administrator role",
                audit = audit,
                companyType = companyType,
            )
        val status =
            EmployeeInviteStatus(
                id = entityId,
                key = TypeKey.restore("ADMIN"),
                name = TypeName.restore("Administrator"),
                audit = audit,
            )
        val invite =
            EmployeeInviteEntity.createNew(
                token = UUID.randomUUID(),
                expirationDate = LocalDate.now().plusDays(14),
                email = "newinvite@example.com",
                userId = 2,
                companyId = 2,
                status = status,
                role = role,
            )

        assertNotNull(invite.id.publicId)
        assertTrue(invite.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertNotNull(invite.token)
        assertEquals(LocalDate.now().plusDays(14), invite.expirationDate)
        assertEquals("newinvite@example.com", invite.email.value)
        assertEquals(2, invite.userId?.value)
        assertEquals(2, invite.companyId.value)
        assertEquals(status, invite.status)
        assertEquals(role, invite.role)
        assertNotNull(invite.audit.createdAt)
        assertTrue(invite.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de EmployeeInviteEntity")
    fun shouldUpdateEmployeeInviteEntity() {
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
                id = EntityId(1, UUID.randomUUID()),
                key = TypeKey("RESTAURANT"),
                name = TypeName("Restaurant"),
                audit = audit,
            )
        val role =
            RoleType(
                id = entityId,
                key = TypeKey.restore("ADMIN"),
                name = TypeName.restore("Administrator"),
                description = "Administrator role",
                audit = audit,
                companyType = companyType,
            )
        val status =
            EmployeeInviteStatus(
                id = entityId,
                key = TypeKey.restore("ADMIN"),
                name = TypeName.restore("Administrator"),
                audit = audit,
            )
        val initialInvite =
            EmployeeInviteEntity.createNew(
                token = UUID.randomUUID(),
                expirationDate = LocalDate.now().plusDays(5),
                email = "initial@example.com",
                userId = null,
                companyId = 3,
                status = status,
                role = role,
            )

        val updatedInvite =
            initialInvite.updateStatus(
                newStatus = status,
            )

        assertEquals(initialInvite.id, updatedInvite.id) // ID should remain the same
        assertEquals(initialInvite.token, updatedInvite.token) // Token should remain the same
        assertEquals(initialInvite.email, updatedInvite.email) // Email should remain the same
        assertEquals(initialInvite.companyId, updatedInvite.companyId) // companyId should remain the same
        assertEquals(status, updatedInvite.status)

        assertNotEquals(initialInvite.audit.updatedAt, updatedInvite.audit.updatedAt) // updatedAt should change
    }
}
