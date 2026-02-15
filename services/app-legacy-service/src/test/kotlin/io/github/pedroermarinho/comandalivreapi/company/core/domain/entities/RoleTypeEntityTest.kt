package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import io.github.pedroermarinho.shared.valueobject.TypeKey
import io.github.pedroermarinho.shared.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para RoleTypeEntity")
class RoleTypeEntityTest {
    @Test
    @DisplayName("Deve criar RoleTypeEntity com todas as propriedades")
    fun shouldCreateRoleTypeEntity() {
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
        val roleType =
            RoleType(
                id = entityId,
                key = TypeKey("MANAGER"),
                name = TypeName("Manager"),
                description = "Manages the team",
                companyType = companyType,
                audit = audit,
            )

        assertEquals(entityId, roleType.id)
        assertEquals("MANAGER", roleType.key.value)
        assertEquals("Manager", roleType.name.value)
        assertEquals("Manages the team", roleType.description)
        assertEquals(companyType, roleType.companyType)
        assertEquals(audit, roleType.audit)
    }
}
