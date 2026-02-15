package io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.address

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

@Schema(description = "Formulário para criação ou atualização de endereços de empresas.")
data class AddressForm(
    @field:NotBlank(message = "A rua é obrigatória.")
    @field:Size(max = 255, message = "A rua deve ter no máximo 255 caracteres.")
    @param:Schema(description = "Nome da rua.", example = "Av. Paulista", required = true)
    val street: String,
    @field:NotBlank(message = "O número é obrigatório.")
    @param:Schema(description = "Número do endereço.", example = "123", required = true)
    val number: String,
    @field:NotBlank(message = "O CEP é obrigatório.")
    @param:Schema(description = "CEP do endereço.", example = "01311-200", required = true)
    val zipCode: String,
    @field:NotBlank(message = "A cidade é obrigatória.")
    @param:Schema(description = "Cidade do endereço.", example = "São Paulo", required = true)
    val city: String,
    @field:NotBlank(message = "O estado é obrigatório.")
    @param:Schema(description = "Estado do endereço.", example = "SP", required = true)
    val state: String,
    @field:NotBlank(message = "O bairro é obrigatório.")
    @param:Schema(description = "Bairro do endereço.", example = "Bela Vista", required = true)
    val neighborhood: String,
    val complement: String? = null,
)
