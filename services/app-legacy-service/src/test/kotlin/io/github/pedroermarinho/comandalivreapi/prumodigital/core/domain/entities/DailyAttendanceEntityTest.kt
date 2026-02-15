package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para DailyAttendanceEntity")
class DailyAttendanceEntityTest {
    @Test
    @DisplayName("Deve criar DailyAttendanceEntity com todas as propriedades")
    fun shouldCreateDailyAttendanceEntity() {
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
        val attendance =
            DailyAttendanceEntity(
                id = entityId,
                dailyReportId = 1,
                employeeAssignmentId = 1,
                present = true,
                arrivalTime = LocalTime.of(8, 0),
                departureTime = LocalTime.of(17, 0),
                attendanceNote = "On time",
                audit = audit,
            )

        assertEquals(entityId, attendance.id)
        assertEquals(1, attendance.dailyReportId)
        assertEquals(1, attendance.employeeAssignmentId)
        assertTrue(attendance.present)
        assertEquals(LocalTime.of(8, 0), attendance.arrivalTime)
        assertEquals(LocalTime.of(17, 0), attendance.departureTime)
        assertEquals("On time", attendance.attendanceNote)
        assertEquals(audit, attendance.audit)
    }

    @Test
    @DisplayName("Deve criar nova DailyAttendanceEntity usando o método de fábrica createNew")
    fun shouldCreateNewDailyAttendanceEntityUsingFactoryMethod() {
        val attendance =
            DailyAttendanceEntity.createNew(
                dailyReportId = 2,
                employeeAssignmentId = 2,
                present = false,
                arrivalTime = null,
                departureTime = null,
                attendanceNote = "Absent",
            )

        assertNotNull(attendance.id.publicId)
        assertTrue(attendance.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(2, attendance.dailyReportId)
        assertEquals(2, attendance.employeeAssignmentId)
        assertFalse(attendance.present)
        assertNull(attendance.arrivalTime)
        assertNull(attendance.departureTime)
        assertEquals("Absent", attendance.attendanceNote)
        assertNotNull(attendance.audit.createdAt)
        assertTrue(attendance.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de DailyAttendanceEntity")
    fun shouldUpdateDailyAttendanceEntity() {
        val initialAttendance =
            DailyAttendanceEntity.createNew(
                dailyReportId = 3,
                employeeAssignmentId = 3,
                present = true,
                arrivalTime = LocalTime.of(9, 0),
                departureTime = null,
                attendanceNote = "Late arrival",
            )

        val updatedAttendance =
            initialAttendance.update(
                present = true,
                arrivalTime = LocalTime.of(9, 15),
                departureTime = LocalTime.of(18, 0),
                attendanceNote = "Updated notes",
            )

        assertEquals(initialAttendance.id, updatedAttendance.id) // ID should remain the same
        assertEquals(initialAttendance.dailyReportId, updatedAttendance.dailyReportId) // dailyReportId should remain the same
        assertEquals(initialAttendance.employeeAssignmentId, updatedAttendance.employeeAssignmentId) // employeeAssignmentId should remain the same
        assertTrue(updatedAttendance.present)
        assertEquals(LocalTime.of(9, 15), updatedAttendance.arrivalTime)
        assertEquals(LocalTime.of(18, 0), updatedAttendance.departureTime)
        assertEquals("Updated notes", updatedAttendance.attendanceNote)
        assertNotEquals(initialAttendance.audit.updatedAt, updatedAttendance.audit.updatedAt) // updatedAt should change
    }
}
