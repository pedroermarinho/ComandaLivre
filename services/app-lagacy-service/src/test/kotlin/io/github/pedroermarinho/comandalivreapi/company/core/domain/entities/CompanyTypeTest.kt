package io.github.pedroermarinho.comandalivreapi.company.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeKey
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@UnitTest
@DisplayName("Teste de unidade para CompanyType")
class CompanyTypeTest {
    @Test
    @DisplayName("Deve criar CompanyType com todas as propriedades")
    fun shouldCreateCompanyType() {
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
                id = entityId,
                key = TypeKey("RESTAURANT"),
                name = TypeName("Restaurant"),
                audit = audit,
            )

        assertEquals(entityId, companyType.id)
        assertEquals("Restaurant", companyType.name.value)
        assertEquals("RESTAURANT", companyType.key.value)
        assertEquals(audit, companyType.audit)
    }
}
