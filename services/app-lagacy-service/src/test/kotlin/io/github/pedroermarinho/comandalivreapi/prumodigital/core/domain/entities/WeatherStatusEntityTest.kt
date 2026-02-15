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
@DisplayName("Teste de unidade para WeatherStatusEntity")
class WeatherStatusEntityTest {
    @Test
    @DisplayName("Deve criar WeatherStatusEntity com todas as propriedades")
    fun shouldCreateWeatherStatusEntity() {
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
        val weather =
            WeatherStatusEntity(
                id = entityId,
                key = "SUNNY",
                name = "Sunny",
                description = "Clear skies, no clouds",
                icon = "sunny.png",
                audit = audit,
            )

        assertEquals(entityId, weather.id)
        assertEquals("SUNNY", weather.key)
        assertEquals("Sunny", weather.name)
        assertEquals("Clear skies, no clouds", weather.description)
        assertEquals("sunny.png", weather.icon)
        assertEquals(audit, weather.audit)
    }
}
