package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityAudit

data class UserAddressEntity(
    val userId: Int,
    val addressId: Int,
    val nickname: String?,
    val tag: String?,
    val isDefault: Boolean,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            userId: Int,
            addressId: Int,
            nickname: String?,
            tag: String?,
            isDefault: Boolean,
        ): UserAddressEntity =
            UserAddressEntity(
                userId = userId,
                addressId = addressId,
                nickname = nickname,
                tag = tag,
                isDefault = isDefault,
                audit = EntityAudit.createNew(),
            )
    }

    fun update(
        nickname: String?,
        tag: String?,
        isDefault: Boolean,
    ): UserAddressEntity =
        this.copy(
            nickname = nickname,
            tag = tag,
            isDefault = isDefault,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
