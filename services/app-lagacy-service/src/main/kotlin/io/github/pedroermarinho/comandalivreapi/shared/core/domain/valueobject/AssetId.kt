package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.BusinessLogicException

@JvmInline
value class AssetId private constructor(
    val value: Int,
) {
    companion object {
        operator fun invoke(value: Int): AssetId {
            if (value <= 0) {
                throw BusinessLogicException("O ID do asset é inválido")
            }
            return AssetId(value)
        }

        fun restore(persistedValue: Int): AssetId = AssetId(persistedValue)
    }
}
