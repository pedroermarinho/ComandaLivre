package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@UnitTest
@DisplayName("Teste de unidade para EntityId")
class EntityIdTest {
    @Test
    @DisplayName("Deve criar um novo EntityId com ID interno 0 e um novo UUID")
    fun shouldCreateNewEntityIdWithZeroInternalIdAndNewGuid() {
        val entityId = EntityId.createNew()

        assertEquals(0, entityId.internalId)
        assertNotNull(entityId.publicId)
    }

    @Test
    @DisplayName("Deve criar um novo EntityId com um UUID fornecido")
    fun shouldCreateNewEntityIdWithProvidedUuid() {
        val providedUuid = UUID.randomUUID()
        val entityId = EntityId.createNew(publicId = providedUuid)

        assertEquals(0, entityId.internalId)
        assertEquals(providedUuid, entityId.publicId)
    }

    @Test
    @DisplayName("isNew deve retornar true para um novo EntityId")
    fun isNewShouldReturnTrueForNewEntityId() {
        val newEntityId = EntityId.createNew()
        assertTrue(newEntityId.isNew())
    }

    @Test
    @DisplayName("isNew deve retornar false para um EntityId existente")
    fun isNewShouldReturnFalseForExistingEntityId() {
        val existingEntityId = EntityId(internalId = 1, publicId = UUID.randomUUID())
        assertFalse(existingEntityId.isNew())
    }

    @Test
    @DisplayName("Deve criar EntityId com UUID e ID válidos")
    fun shouldCreateEntityIdWithValidUuidAndId() {
        val uuid = UUID.randomUUID()
        val id = 123
        val entityId = EntityId(internalId = id, publicId = uuid)

        assertEquals(uuid, entityId.publicId)
        assertEquals(id, entityId.internalId)
    }
}
