package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.UserName
import io.github.pedroermarinho.shared.valueobject.AssetId
import io.github.pedroermarinho.shared.valueobject.EmailAddress
import io.github.pedroermarinho.shared.valueobject.EntityAudit
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.util.*

data class UserEntity(
    val id: EntityId,
    val sub: String,
    val name: UserName,
    val email: EmailAddress,
    val avatarAssetId: AssetId?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            sub: String,
            name: String,
            email: String,
            avatarAssetId: Int?,
        ): UserEntity =
            UserEntity(
                id = EntityId.createNew(publicId = publicId),
                sub = sub,
                name = UserName(name),
                email = EmailAddress(email),
                avatarAssetId = avatarAssetId?.let { AssetId(it) },
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(name: String): UserEntity =
        this.copy(
            name = UserName(name),
            audit = this.audit.update(),
        )

    fun updatePhoto(assetId: Int): UserEntity =
        this.copy(
            avatarAssetId = AssetId(assetId),
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
