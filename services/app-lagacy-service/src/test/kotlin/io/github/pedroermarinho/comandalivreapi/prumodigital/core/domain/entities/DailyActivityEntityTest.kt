package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para DailyActivityEntity")
class DailyActivityEntityTest {
    @Test
    @DisplayName("Deve criar DailyActivityEntity com todas as propriedades")
    fun shouldCreateDailyActivityEntity() {
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
        val activity =
            DailyActivityEntity(
                id = entityId,
                dailyReportId = 1,
                activityDescription = "Site inspection",
                statusId = 1,
                locationDescription = "Building A",
                responsibleEmployeeAssignmentId = 1,
                audit = audit,
            )

        assertEquals(entityId, activity.id)
        assertEquals(1, activity.dailyReportId)
        assertEquals("Site inspection", activity.activityDescription)
        assertEquals(1, activity.statusId)
        assertEquals("Building A", activity.locationDescription)
        assertEquals(1, activity.responsibleEmployeeAssignmentId)
        assertEquals(audit, activity.audit)
    }

    @Test
    @DisplayName("Deve criar nova DailyActivityEntity usando o método de fábrica createNew")
    fun shouldCreateNewDailyActivityEntityUsingFactoryMethod() {
        val activity =
            DailyActivityEntity.createNew(
                dailyReportId = 2,
                activityDescription = "Material delivery",
                statusId = 2,
                locationDescription = "Warehouse",
                responsibleEmployeeAssignmentId = 2,
            )

        assertNotNull(activity.id.publicId)
        assertTrue(activity.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(2, activity.dailyReportId)
        assertEquals("Material delivery", activity.activityDescription)
        assertEquals(2, activity.statusId)
        assertEquals("Warehouse", activity.locationDescription)
        assertEquals(2, activity.responsibleEmployeeAssignmentId)
        assertNotNull(activity.audit.createdAt)
        assertTrue(activity.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de DailyActivityEntity")
    fun shouldUpdateDailyActivityEntity() {
        val initialActivity =
            DailyActivityEntity.createNew(
                dailyReportId = 3,
                activityDescription = "Initial activity",
                statusId = 1,
                locationDescription = "Initial location",
                responsibleEmployeeAssignmentId = 3,
            )

        val updatedActivity =
            initialActivity.update(
                activityDescription = "Updated activity",
                statusId = 3,
                locationDescription = "Updated location",
                responsibleEmployeeAssignmentId = 4,
            )

        assertEquals(initialActivity.id, updatedActivity.id) // ID should remain the same
        assertEquals(initialActivity.dailyReportId, updatedActivity.dailyReportId) // dailyReportId should remain the same
        assertEquals("Updated activity", updatedActivity.activityDescription)
        assertEquals(3, updatedActivity.statusId)
        assertEquals("Updated location", updatedActivity.locationDescription)
        assertEquals(4, updatedActivity.responsibleEmployeeAssignmentId)
        assertNotEquals(initialActivity.audit.updatedAt, updatedActivity.audit.updatedAt) // updatedAt should change
    }
}
