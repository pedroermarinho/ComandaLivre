package io.github.pedroermarinho.shared.valueobject

import java.util.UUID
import com.github.f4b6a3.uuid.UuidCreator

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