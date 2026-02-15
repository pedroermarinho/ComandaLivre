package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para AddressEntity")
class AddressEntityTest {
    @Test
    @DisplayName("Deve criar AddressEntity com todas as propriedades")
    fun shouldCreateAddressEntity() {
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
        val address =
            AddressEntity(
                id = entityId,
                street = Street("Main St"),
                number = "123",
                zipCode = ZipCode("12345-678"),
                city = City("Anytown"),
                state = State("AS"),
                neighborhood = Neighborhood("Downtown"),
                complement = "Apt 1A",
                audit = audit,
            )

        assertEquals(entityId, address.id)
        assertEquals(Street("Main St"), address.street)
        assertEquals("123", address.number)
        assertEquals(ZipCode("12345-678"), address.zipCode)
        assertEquals(City("Anytown"), address.city)
        assertEquals(State("AS"), address.state)
        assertEquals(Neighborhood("Downtown"), address.neighborhood)
        assertEquals("Apt 1A", address.complement)
        assertEquals(audit, address.audit)
    }

    @Test
    @DisplayName("Deve criar nova AddressEntity usando o método de fábrica createNew")
    fun shouldCreateNewAddressEntityUsingFactoryMethod() {
        val address =
            AddressEntity.createNew(
                street = "Second St",
                number = "456",
                zipCode = "87654-321",
                city = "Otherville",
                state = "OS",
                neighborhood = "Uptown",
                complement = null,
            )

        assertNotNull(address.id.publicId)
        assertTrue(address.id.internalId == 0) // Assuming 0 for new entities before persistence
        assertEquals(Street("Second St"), address.street)
        assertEquals("456", address.number)
        assertEquals(ZipCode("87654-321"), address.zipCode)
        assertEquals(City("Otherville"), address.city)
        assertEquals(State("OS"), address.state)
        assertEquals(Neighborhood("Uptown"), address.neighborhood)
        assertNull(address.complement)
        assertNotNull(address.audit.createdAt)
        assertTrue(address.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de AddressEntity")
    fun shouldUpdateAddressEntity() {
        val initialAddress =
            AddressEntity.createNew(
                street = "Old St",
                number = "789",
                zipCode = "11111-111",
                city = "Old City",
                state = "OC",
                neighborhood = "Old Town",
                complement = "Old Comp",
            )

        val updatedAddress =
            initialAddress.update(
                street = "New St",
                number = "987",
                zipCode = "22222-222",
                city = "New City",
                state = "NC",
                neighborhood = "New Town",
                complement = "New Comp",
            )

        assertEquals(initialAddress.id, updatedAddress.id) // ID should remain the same
        assertEquals(Street("New St"), updatedAddress.street)
        assertEquals("987", updatedAddress.number)
        assertEquals(ZipCode("22222-222"), updatedAddress.zipCode)
        assertEquals(City("New City"), updatedAddress.city)
        assertEquals(State("NC"), updatedAddress.state)
        assertEquals(Neighborhood("New Town"), updatedAddress.neighborhood)
        assertEquals("New Comp", updatedAddress.complement)
        assertNotEquals(initialAddress.audit.updatedAt, updatedAddress.audit.updatedAt) // updatedAt should change
    }
}
