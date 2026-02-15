package io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.valueobject.TypeName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para TypeName")
class TypeNameTest {
    @Test
    @DisplayName("Deve criar TypeName com valor válido")
    fun shouldCreateTypeNameWithValidValue() {
        val typeName = TypeName("Nome Válido")
        assertEquals("Nome Válido", typeName.value)
    }

    @Test
    @DisplayName("Deve normalizar o valor removendo espaços")
    fun shouldNormalizeValueByTrimming() {
        val typeName = TypeName("  Nome com espaços  ")
        assertEquals("Nome com espaços", typeName.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor em branco")
    fun shouldThrowExceptionForBlankValue() {
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                TypeName("   ")
            }
        assertEquals("O nome do tipo não pode ser vazia", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar TypeName de um valor persistido")
    fun shouldRestoreTypeNameFromPersistedValue() {
        val persistedValue = "Nome Persistido"
        val typeName = TypeName.restore(persistedValue)
        assertEquals(persistedValue, typeName.value)
    }
}
