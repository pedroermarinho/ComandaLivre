package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para EmployeeProjectAssignmentEntity")
class EmployeeProjectAssignmentEntityTest {
    @Test
    @DisplayName("Deve criar EmployeeProjectAssignmentEntity com todas as propriedades")
    fun shouldCreateEmployeeProjectAssignmentEntity() {
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
        val assignment =
            EmployeeProjectAssignmentEntity(
                id = entityId,
                employeeId = 1,
                projectId = 1,
                roleInProjectId = 1,
                assignmentStartDate = LocalDate.now(),
                assignmentEndDate = null,
                isActiveAssignment = true,
                isProjectAdmin = false,
                audit = audit,
            )

        assertEquals(entityId, assignment.id)
        assertEquals(1, assignment.employeeId)
        assertEquals(1, assignment.projectId)
        assertEquals(1, assignment.roleInProjectId)
        assertEquals(LocalDate.now(), assignment.assignmentStartDate)
        assertNull(assignment.assignmentEndDate)
        assertTrue(assignment.isActiveAssignment)
        assertFalse(assignment.isProjectAdmin)
        assertEquals(audit, assignment.audit)
    }

    @Test
    @DisplayName("Deve criar nova EmployeeProjectAssignmentEntity usando o método de fábrica createNew")
    fun shouldCreateNewEmployeeProjectAssignmentEntityUsingFactoryMethod() {
        val assignment =
            EmployeeProjectAssignmentEntity.createNew(
                employeeId = 2,
                projectId = 2,
                roleInProjectId = 2,
                assignmentStartDate = LocalDate.now().minusDays(10),
                assignmentEndDate = LocalDate.now().plusDays(50),
                isActiveAssignment = true,
                isProjectAdmin = true,
            )

        assertNotNull(assignment.id.publicId)
        assertTrue(assignment.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(2, assignment.employeeId)
        assertEquals(2, assignment.projectId)
        assertEquals(2, assignment.roleInProjectId)
        assertEquals(LocalDate.now().minusDays(10), assignment.assignmentStartDate)
        assertEquals(LocalDate.now().plusDays(50), assignment.assignmentEndDate)
        assertTrue(assignment.isActiveAssignment)
        assertTrue(assignment.isProjectAdmin)
        assertNotNull(assignment.audit.createdAt)
        assertTrue(assignment.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de EmployeeProjectAssignmentEntity")
    fun shouldUpdateEmployeeProjectAssignmentEntity() {
        val initialAssignment =
            EmployeeProjectAssignmentEntity.createNew(
                employeeId = 3,
                projectId = 3,
                roleInProjectId = 3,
                assignmentStartDate = LocalDate.now().minusDays(5),
                assignmentEndDate = null,
                isActiveAssignment = true,
                isProjectAdmin = false,
            )

        val updatedAssignment =
            initialAssignment.update(
                roleInProjectId = 4,
                assignmentStartDate = LocalDate.now().minusDays(5),
                assignmentEndDate = LocalDate.now().plusDays(100),
                isActiveAssignment = false,
                isProjectAdmin = true,
            )

        assertEquals(initialAssignment.id, updatedAssignment.id) // ID should remain the same
        assertEquals(initialAssignment.employeeId, updatedAssignment.employeeId) // employeeId should remain the same
        assertEquals(initialAssignment.projectId, updatedAssignment.projectId) // projectId should remain the same
        assertEquals(4, updatedAssignment.roleInProjectId)
        assertEquals(LocalDate.now().minusDays(5), updatedAssignment.assignmentStartDate)
        assertEquals(LocalDate.now().plusDays(100), updatedAssignment.assignmentEndDate)
        assertFalse(updatedAssignment.isActiveAssignment)
        assertTrue(updatedAssignment.isProjectAdmin)
        assertNotEquals(initialAssignment.audit.updatedAt, updatedAssignment.audit.updatedAt) // updatedAt should change
    }
}
