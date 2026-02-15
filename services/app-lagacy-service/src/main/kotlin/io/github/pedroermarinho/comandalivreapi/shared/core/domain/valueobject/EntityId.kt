package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

data class EntityId(
    val internalId: Int,
    val publicId: UUID,
) {
    companion object {
        fun createNew(publicId: UUID? = null): EntityId =
            EntityId(
                internalId = 0,
                publicId = publicId ?: UuidCreator.getTimeOrderedEpoch(),
            )
    }

    fun isNew(): Boolean = internalId == 0
}
