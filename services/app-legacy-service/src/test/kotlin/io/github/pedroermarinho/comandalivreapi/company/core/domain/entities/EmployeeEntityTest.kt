package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyId
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import io.github.pedroermarinho.shared.valueobject.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para EmployeeEntity")
class EmployeeEntityTest {
    @Test
    @DisplayName("Deve criar EmployeeEntity com todas as propriedades")
    fun shouldCreateEmployeeEntity() {
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

        val employee =
            EmployeeEntity(
                id = entityId,
                role = role,
                companyId = CompanyId(1),
                userId = UserId(1),
                status = true,
                audit = audit,
            )

        assertEquals(entityId, employee.id)
        assertEquals(role, employee.role)
        assertEquals(CompanyId(1), employee.companyId)
        assertEquals(UserId(1), employee.userId)
        assertTrue(employee.status)
        assertEquals(audit, employee.audit)
    }

    @Test
    @DisplayName("Deve criar nova EmployeeEntity usando o método de fábrica createNew")
    fun shouldCreateNewEmployeeEntityUsingFactoryMethod() {
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

        val employee =
            EmployeeEntity.createNew(
                role = role,
                companyId = 2,
                userId = 2,
                status = false,
            )

        assertNotNull(employee.id.publicId)
        assertTrue(employee.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(role, employee.role)
        assertEquals(CompanyId(2), employee.companyId)
        assertEquals(UserId(2), employee.userId)
        assertFalse(employee.status)
        assertNotNull(employee.audit.createdAt)
        assertTrue(employee.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de EmployeeEntity")
    fun shouldUpdateEmployeeEntity() {
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
        val initialEmployee =
            EmployeeEntity.createNew(
                role = role,
                companyId = 1,
                userId = 1,
                status = true,
            )

        val updatedEmployee =
            initialEmployee.updateStatus(
                newStatus = false,
            )

        assertEquals(initialEmployee.id, updatedEmployee.id) // ID should remain the same
        assertEquals(initialEmployee.companyId, updatedEmployee.companyId) // companyId should remain the same
        assertEquals(initialEmployee.userId, updatedEmployee.userId) // userId should remain the same
        assertEquals(role, updatedEmployee.role)
        assertFalse(updatedEmployee.status)
        assertNotEquals(initialEmployee.audit.updatedAt, updatedEmployee.audit.updatedAt) // updatedAt should change
    }
}
