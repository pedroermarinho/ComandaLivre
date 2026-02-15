package io.github.pedroermarinho.comandalivreapi.util.factory

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.employee.RoleTypeDTO
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.RoleTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.CompanyType
import io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject.RoleType
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserAuthDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.UserEntity
import io.github.pedroermarinho.shared.valueobject.*
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import java.time.LocalDateTime

object MockUserFactory {
    fun build(): UserDTO =
        UserDTO(
            id = EntityId(MockConstants.USER_ID_INT, MockConstants.USER_ID_UUID),
            sub = MockConstants.USER_SUB,
            name = MockConstants.USER_NAME,
            email = MockConstants.USER_EMAIL,
            avatarAssetId = null,
            featureKeys = listOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deletedAt = null,
        )

    fun buildUserAuthDTO(): UserAuthDTO =
        UserAuthDTO(
            sub = MockConstants.USER_SUB,
            email = MockConstants.USER_EMAIL,
            emailVerified = true,
            name = MockConstants.USER_NAME,
            picture = null,
        )

    fun buildUserEntity(): UserEntity =
        UserEntity.createNew(
            sub = MockConstants.USER_SUB,
            name = MockConstants.USER_NAME,
            email = MockConstants.USER_EMAIL,
            avatarAssetId = null,
        )

    fun buildRoleType(): RoleTypeDTO =
        RoleTypeDTO(
            id = EntityId(MockConstants.ROLE_TYPE_ID_INT, MockConstants.ROLE_TYPE_ID_UUID),
            name = MockConstants.ROLE_TYPE_NAME,
            key = RoleTypeEnum.RESTAURANT_OWNER.value,
        )

    fun buildRoleTypeValueObject(): RoleType {
        val companyType =
            CompanyType(
                id = EntityId(MockConstants.COMPANY_TYPE_ID_INT, MockConstants.COMPANY_TYPE_ID_UUID),
                key = TypeKey.restore(CompanyTypeEnum.RESTAURANT.value),
                name = TypeName("Restaurante"),
                audit = EntityAudit.createNew(),
            )
        return RoleType(
            id = EntityId(MockConstants.ROLE_TYPE_ID_INT, MockConstants.ROLE_TYPE_ID_UUID),
            name = TypeName(MockConstants.ROLE_TYPE_NAME),
            key = TypeKey.restore(RoleTypeEnum.RESTAURANT_OWNER.value),
            description = "",
            companyType = companyType,
            audit = EntityAudit.createNew(),
        )
    }
}
