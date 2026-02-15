package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.AssetId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EmailAddress
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para UserEntity")
class UserEntityTest {
    @Test
    @DisplayName("Deve criar UserEntity com todas as propriedades")
    fun shouldCreateUserEntity() {
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
        val user =
            UserEntity(
                id = entityId,
                sub = "oauth|12345",
                name = UserName("Test User"),
                email = EmailAddress("test@example.com"),
                avatarAssetId = AssetId(1),
                audit = audit,
            )

        assertEquals(entityId, user.id)
        assertEquals("oauth|12345", user.sub)
        assertEquals(UserName("Test User"), user.name)
        assertEquals(EmailAddress("test@example.com"), user.email)
        assertEquals(AssetId(1), user.avatarAssetId)
        assertEquals(audit, user.audit)
    }

    @Test
    @DisplayName("Deve criar nova UserEntity usando o método de fábrica createNew")
    fun shouldCreateNewUserEntity() {
        val user =
            UserEntity.createNew(
                sub = "oauth|67890",
                name = "New User",
                email = "new.user@example.com",
                avatarAssetId = null,
            )

        assertNotNull(user.id.publicId)
        assertTrue(user.id.internalId == 0)
        assertEquals("oauth|67890", user.sub)
        assertEquals(UserName("New User"), user.name)
        assertEquals(EmailAddress("new.user@example.com"), user.email)
        assertNull(user.avatarAssetId)
        assertNotNull(user.audit.createdAt)
        assertTrue(user.isNew())
    }

    @Test
    @DisplayName("Deve atualizar as propriedades de UserEntity")
    fun shouldUpdateUserEntity() {
        val initialUser =
            UserEntity.createNew(
                sub = "oauth|11111",
                name = "Initial User",
                email = "initial.user@example.com",
                avatarAssetId = null,
            )

        val updatedUser =
            initialUser.update(
                name = "Updated User",
            )

        assertEquals(initialUser.id, updatedUser.id)
        assertEquals(initialUser.sub, updatedUser.sub)
        assertEquals(UserName("Updated User"), updatedUser.name)
        assertEquals(initialUser.email, updatedUser.email)
        assertNotEquals(initialUser.audit.updatedAt, updatedUser.audit.updatedAt)
    }
}
