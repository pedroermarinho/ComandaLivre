package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.valueobject.ReportText
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para DailyReportEntity")
class DailyReportEntityTest {
    @Test
    @DisplayName("Deve criar DailyReportEntity com todas as propriedades")
    fun shouldCreateDailyReportEntity() {
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
        val dailyReport =
            DailyReportEntity(
                id = entityId,
                projectId = 1,
                reportDate = LocalDate.now(),
                generalObservations = ReportText("General observations"),
                morningWeatherId = 1,
                afternoonWeatherId = 2,
                workStartTime = LocalTime.of(8, 0),
                lunchStartTime = LocalTime.of(12, 0),
                lunchEndTime = LocalTime.of(13, 0),
                workEndTime = LocalTime.of(17, 0),
                reportedByAssignmentId = 1,
                audit = audit,
            )

        assertEquals(entityId, dailyReport.id)
        assertEquals(1, dailyReport.projectId)
        assertEquals(LocalDate.now(), dailyReport.reportDate)
        assertEquals(ReportText("General observations"), dailyReport.generalObservations)
        assertEquals(1, dailyReport.morningWeatherId)
        assertEquals(2, dailyReport.afternoonWeatherId)
        assertEquals(LocalTime.of(8, 0), dailyReport.workStartTime)
        assertEquals(LocalTime.of(12, 0), dailyReport.lunchStartTime)
        assertEquals(LocalTime.of(13, 0), dailyReport.lunchEndTime)
        assertEquals(LocalTime.of(17, 0), dailyReport.workEndTime)
        assertEquals(1, dailyReport.reportedByAssignmentId)
        assertEquals(audit, dailyReport.audit)
    }

    @Test
    @DisplayName("Deve criar nova DailyReportEntity usando o método de fábrica createNew")
    fun shouldCreateNewDailyReportEntityUsingFactoryMethod() {
        val dailyReport =
            DailyReportEntity.createNew(
                projectId = 2,
                reportDate = LocalDate.now().minusDays(1),
                generalObservations = "New observations",
                morningWeatherId = 3,
                afternoonWeatherId = 4,
                workStartTime = LocalTime.of(9, 0),
                lunchStartTime = LocalTime.of(13, 0),
                lunchEndTime = LocalTime.of(14, 0),
                workEndTime = LocalTime.of(18, 0),
                reportedByAssignmentId = 2,
            )

        assertNotNull(dailyReport.id.publicId)
        assertTrue(dailyReport.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(2, dailyReport.projectId)
        assertEquals(LocalDate.now().minusDays(1), dailyReport.reportDate)
        assertEquals(ReportText("New observations"), dailyReport.generalObservations)
        assertEquals(3, dailyReport.morningWeatherId)
        assertEquals(4, dailyReport.afternoonWeatherId)
        assertEquals(LocalTime.of(9, 0), dailyReport.workStartTime)
        assertEquals(LocalTime.of(13, 0), dailyReport.lunchStartTime)
        assertEquals(LocalTime.of(14, 0), dailyReport.lunchEndTime)
        assertEquals(LocalTime.of(18, 0), dailyReport.workEndTime)
        assertEquals(2, dailyReport.reportedByAssignmentId)
        assertNotNull(dailyReport.audit.createdAt)
        assertTrue(dailyReport.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de DailyReportEntity")
    fun shouldUpdateDailyReportEntity() {
        val initialDailyReport =
            DailyReportEntity.createNew(
                projectId = 1,
                reportDate = LocalDate.now(),
                generalObservations = "Initial observations",
                morningWeatherId = 1,
                afternoonWeatherId = 2,
                workStartTime = LocalTime.of(8, 0),
                lunchStartTime = LocalTime.of(12, 0),
                lunchEndTime = LocalTime.of(13, 0),
                workEndTime = LocalTime.of(17, 0),
                reportedByAssignmentId = 1,
            )

        val updatedDailyReport =
            initialDailyReport.update(
                generalObservations = "Updated observations",
                morningWeatherId = 5,
                afternoonWeatherId = 6,
                workStartTime = LocalTime.of(7, 0),
                lunchStartTime = LocalTime.of(11, 0),
                lunchEndTime = LocalTime.of(12, 0),
                workEndTime = LocalTime.of(16, 0),
                reportedByAssignmentId = 3,
            )

        assertEquals(initialDailyReport.id, updatedDailyReport.id) // ID should remain the same
        assertEquals(initialDailyReport.projectId, updatedDailyReport.projectId) // projectId should remain the same
        assertEquals(initialDailyReport.reportDate, updatedDailyReport.reportDate) // reportDate should remain the same
        assertEquals(ReportText("Updated observations"), updatedDailyReport.generalObservations)
        assertEquals(5, updatedDailyReport.morningWeatherId)
        assertEquals(6, updatedDailyReport.afternoonWeatherId)
        assertEquals(LocalTime.of(7, 0), updatedDailyReport.workStartTime)
        assertEquals(LocalTime.of(11, 0), updatedDailyReport.lunchStartTime)
        assertEquals(LocalTime.of(12, 0), updatedDailyReport.lunchEndTime)
        assertEquals(LocalTime.of(16, 0), updatedDailyReport.workEndTime)
        assertEquals(3, updatedDailyReport.reportedByAssignmentId)
        assertNotEquals(initialDailyReport.audit.updatedAt, updatedDailyReport.audit.updatedAt) // updatedAt should change
    }
}
