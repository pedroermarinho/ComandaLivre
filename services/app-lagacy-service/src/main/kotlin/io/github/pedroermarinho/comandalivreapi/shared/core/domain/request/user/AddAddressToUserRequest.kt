package io.github.pedroermarinho.comandalivreapi.shared.core.domain.request.user

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address.AddressForm
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Formulário para associar um novo endereço a um usuário.")
data class AddAddressToUserRequest(
    @field:NotNull(message = "Os dados do endereço são obrigatórios.")
    @field:Valid
    @param:Schema(description = "Objeto com os detalhes do endereço a ser adicionado.", required = true)
    val address: AddressForm,
    @field:NotBlank(message = "O apelido do endereço é obrigatório.")
    @param:Schema(description = "Apelido para identificar o endereço.", example = "Casa", required = true)
    val nickname: String,
    @param:Schema(description = "Tag para categorizar o endereço.", example = "home")
    val tag: String?,
)
