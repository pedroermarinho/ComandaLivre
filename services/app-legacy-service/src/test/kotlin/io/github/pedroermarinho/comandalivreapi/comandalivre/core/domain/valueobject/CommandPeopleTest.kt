package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.valueobject

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTest
@DisplayName("Teste de unidade para CommandPeople")
class CommandPeopleTest {
    @Test
    @DisplayName("Deve criar CommandPeople com valor positivo válido")
    fun shouldCreateCommandPeopleWithValidPositiveValue() {
        val validPeople = 1
        val commandPeople = CommandPeople(validPeople)
        assertEquals(validPeople, commandPeople.value)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor zero")
    fun shouldThrowExceptionForZeroValue() {
        val zeroPeople = 0
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandPeople(zeroPeople)
            }
        assertEquals("Número de pessoas deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor negativo")
    fun shouldThrowExceptionForNegativeValue() {
        val negativePeople = -1
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandPeople(negativePeople)
            }
        assertEquals("Número de pessoas deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve lançar BusinessLogicException para valor acima de 1000")
    fun shouldThrowExceptionForValueGreaterThan1000() {
        val excessivePeople = 1001
        val exception =
            assertThrows(BusinessLogicException::class.java) {
                CommandPeople(excessivePeople)
            }
        assertEquals("Número de pessoas deve ser entre 1 e 1000", exception.message)
    }

    @Test
    @DisplayName("Deve restaurar CommandPeople de um valor persistido")
    fun shouldRestoreCommandPeopleFromPersistedValue() {
        val persistedValue = 5
        val commandPeople = CommandPeople.restore(persistedValue)
        assertEquals(persistedValue, commandPeople.value)
    }
}
