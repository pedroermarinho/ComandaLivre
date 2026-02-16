package io.github.pedroermarinho.company.domain.request.company

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Formulário para a atualização de uma empresa existente.")
data class CompanyUpdateRequest(
    @field:NotBlank(message = "O nome da empresa é obrigatório.")
    @field:Size(min = 2, max = 255, message = "O nome da empresa deve ter entre 2 e 255 caracteres.")
    @param:Schema(description = "Novo nome fantasia ou principal da empresa.", example = "Restaurante Sabor do Brasil", required = true)
    val name: String,
    @field:Email(message = "O formato do email é inválido.")
    @param:Schema(description = "Novo email principal de contato da empresa.", example = "contato@sabordobrasil.com")
    val email: String? = null,
    @param:Schema(description = "Novo telefone principal de contato da empresa.", example = "+5511987654321")
    val phone: String? = null,
    @field:Size(min = 14, max = 18, message = "O CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX.")
    @param:Schema(description = "Novo CNPJ da empresa.", example = "98.765.432/0001-10")
    val cnpj: String? = null,
    @param:Schema(description = "Nova descrição sobre a empresa ou restaurante.", example = "Especializado em culinária brasileira regional e contemporânea.")
    val description: String? = null,
    @param:Schema(description = "Define se a empresa é publicamente listável.", example = "true", defaultValue = "true")
    val isPublic: Boolean = true,
)
