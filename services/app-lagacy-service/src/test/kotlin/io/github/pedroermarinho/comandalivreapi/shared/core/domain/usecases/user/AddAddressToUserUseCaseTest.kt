
package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.user

import io.github.pedroermarinho.comandalivreapi.annotations.UnitTest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.user.AddAddressToUserForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.UserAddressRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.request.user.AddAddressToUserRequest
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.address.CreateAddressUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import io.github.pedroermarinho.comandalivreapi.util.MockConstants
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@UnitTest
@ExtendWith(MockKExtension::class)
@DisplayName("Caso de uso: Adicionar endereço ao usuário")
class AddAddressToUserUseCaseTest {
    @MockK
    private lateinit var userAddressRepository: UserAddressRepository

    @MockK
    private lateinit var currentUserUseCase: CurrentUserUseCase

    @MockK
    private lateinit var createAddressUseCase: CreateAddressUseCase

    @InjectMockKs
    private lateinit var addAddressToUserUseCase: AddAddressToUserUseCase

    @Test
    @DisplayName("Deve adicionar um endereço a um usuário com sucesso")
    fun `add address to user successfully`() {
        // Given
        val userId = MockConstants.USER_ID_INT
        val addressForm = AddressForm("street", "123", "neighborhood", "city", "state", "12345-678", null)
        val request = AddAddressToUserRequest(address = addressForm, nickname = "Home", tag = "HOME")
        val addressId = EntityId(1, UUID.randomUUID())

        every { currentUserUseCase.getUserId() } returns Result.success(userId)
        every { currentUserUseCase.checkUserEditingOwnResource(userId) } returns Result.success(Unit)
        every { createAddressUseCase.create(addressForm) } returns Result.success(addressId)
        every { userAddressRepository.addAddressToUser(userId, any<AddAddressToUserForm>()) } returns Result.success(Unit)

        // When
        val result = addAddressToUserUseCase.execute(request)

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { createAddressUseCase.create(addressForm) }
        verify(exactly = 1) { userAddressRepository.addAddressToUser(userId, any<AddAddressToUserForm>()) }
    }
}
