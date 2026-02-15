package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ClientName
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ProjectCode
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ProjectName
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.MonetaryValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para ProjectEntity")
class ProjectEntityTest {
    @Test
    @DisplayName("Deve criar ProjectEntity com todas as propriedades")
    fun shouldCreateProjectEntity() {
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
        val project =
            ProjectEntity(
                id = entityId,
                companyId = 1,
                name = ProjectName("Project X"),
                code = ProjectCode("PX-001"),
                addressId = null,
                plannedStartDate = LocalDate.now(),
                plannedEndDate = LocalDate.now().plusMonths(6),
                actualStartDate = LocalDate.now(),
                actualEndDate = null,
                clientName = ClientName("Client A"),
                projectStatusId = 1,
                budget = MonetaryValue(BigDecimal("100000.00")),
                description = "Description for Project X",
                audit = audit,
            )

        assertEquals(entityId, project.id)
        assertEquals(1, project.companyId)
        assertEquals(ProjectName("Project X"), project.name)
        assertEquals(ProjectCode("PX-001"), project.code)
        assertNull(project.addressId)
        assertEquals(LocalDate.now(), project.plannedStartDate)
        assertEquals(LocalDate.now().plusMonths(6), project.plannedEndDate)
        assertEquals(LocalDate.now(), project.actualStartDate)
        assertNull(project.actualEndDate)
        assertEquals(ClientName("Client A"), project.clientName)
        assertEquals(1, project.projectStatusId)
        assertEquals(MonetaryValue(BigDecimal("100000.00")), project.budget)
        assertEquals("Description for Project X", project.description)
        assertEquals(audit, project.audit)
    }

    @Test
    @DisplayName("Deve criar nova ProjectEntity usando o método de fábrica createNew")
    fun shouldCreateNewProjectEntityUsingFactoryMethod() {
        val project =
            ProjectEntity.createNew(
                companyId = 2,
                name = "New Project",
                code = "NP-002",
                addressId = 10,
                plannedStartDate = LocalDate.now().plusDays(10),
                plannedEndDate = LocalDate.now().plusMonths(12),
                actualStartDate = null,
                actualEndDate = null,
                clientName = "Client B",
                projectStatusId = 2,
                budget = BigDecimal("200000.00"),
                description = "Description for New Project",
            )

        assertNotNull(project.id.publicId)
        assertTrue(project.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(2, project.companyId)
        assertEquals(ProjectName("New Project"), project.name)
        assertEquals(ProjectCode("NP-002"), project.code)
        assertEquals(10, project.addressId)
        assertEquals(LocalDate.now().plusDays(10), project.plannedStartDate)
        assertEquals(LocalDate.now().plusMonths(12), project.plannedEndDate)
        assertNull(project.actualStartDate)
        assertNull(project.actualEndDate)
        assertEquals(ClientName("Client B"), project.clientName)
        assertEquals(2, project.projectStatusId)
        assertEquals(MonetaryValue(BigDecimal("200000.00")), project.budget)
        assertEquals("Description for New Project", project.description)
        assertNotNull(project.audit.createdAt)
        assertTrue(project.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de ProjectEntity")
    fun shouldUpdateProjectEntity() {
        val initialProject =
            ProjectEntity.createNew(
                companyId = 3,
                name = "Initial Project",
                code = "IP-003",
                addressId = null,
                plannedStartDate = LocalDate.now(),
                plannedEndDate = LocalDate.now().plusMonths(3),
                actualStartDate = LocalDate.now(),
                actualEndDate = null,
                clientName = "Client C",
                projectStatusId = 1,
                budget = BigDecimal("50000.00"),
                description = "Initial description",
            )

        val updatedProject =
            initialProject.update(
                name = "Updated Project",
                code = "UP-003",
                addressId = 20,
                plannedStartDate = LocalDate.now().minusDays(5),
                plannedEndDate = LocalDate.now().plusMonths(9),
                actualStartDate = LocalDate.now().minusDays(2),
                actualEndDate = LocalDate.now().plusMonths(8),
                clientName = "Client D",
                projectStatusId = 3,
                budget = BigDecimal("75000.00"),
                description = "Updated description",
            )

        assertEquals(initialProject.id, updatedProject.id) // ID should remain the same
        assertEquals(initialProject.companyId, updatedProject.companyId) // companyId should remain the same
        assertEquals(ProjectName("Updated Project"), updatedProject.name)
        assertEquals(ProjectCode("UP-003"), updatedProject.code)
        assertEquals(20, updatedProject.addressId)
        assertEquals(LocalDate.now().minusDays(5), updatedProject.plannedStartDate)
        assertEquals(LocalDate.now().plusMonths(9), updatedProject.plannedEndDate)
        assertEquals(LocalDate.now().minusDays(2), updatedProject.actualStartDate)
        assertEquals(LocalDate.now().plusMonths(8), updatedProject.actualEndDate)
        assertEquals(ClientName("Client D"), updatedProject.clientName)
        assertEquals(3, updatedProject.projectStatusId)
        assertEquals(MonetaryValue(BigDecimal("75000.00")), updatedProject.budget)
        assertEquals("Updated description", updatedProject.description)
        assertNotEquals(initialProject.audit.updatedAt, updatedProject.audit.updatedAt) // updatedAt should change
    }
}
