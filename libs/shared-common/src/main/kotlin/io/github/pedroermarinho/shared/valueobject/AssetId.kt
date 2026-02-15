package io.github.pedroermarinho.shared.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

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
