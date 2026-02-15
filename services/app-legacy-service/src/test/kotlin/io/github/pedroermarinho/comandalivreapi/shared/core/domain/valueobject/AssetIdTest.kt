package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.valueobject.AssetId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para AssetId")
class AssetIdTest {
    @Test
    @DisplayName("Deve criar AssetId com valor positivo válido")
    fun shouldCreateAssetIdWithValidPositiveValue() {
        val validId = 1
        val assetId = AssetId(validId)
        assertEquals(validId, assetId.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroId = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                AssetId(zeroId)
            }
        assertEquals("O ID do asset é inválido", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativeId = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                AssetId(negativeId)
            }
        assertEquals("O ID do asset é inválido", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar AssetId de um valor persistido")
    fun shouldRestoreAssetIdFromPersistedValue() {
        val persistedValue = 10
        val assetId = AssetId.restore(persistedValue)
        assertEquals(persistedValue, assetId.value)
    }
}
