package io.github.pedroermarinho.company.domain.request.company

import io.github.pedroermarinho.comandalivreapi.company.core.domain.enums.CompanyTypeEnum
import io.github.pedroermarinho.comandalivreapi.company.core.domain.validation.UniqueCompanyName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

@Schema(description = "Formulário para a criação de uma nova empresa ou restaurante.")
data class CompanyCreateRequest(
    @param:Schema(description = "ID público da empresa. Gerado automaticamente se não for fornecido.")
    val publicId: UUID? = null,
    @field:NotBlank(message = "O nome da empresa é obrigatório.")
    @field:Size(min = 2, max = 255, message = "O nome da empresa deve ter entre 2 e 255 caracteres.")
    @field:UniqueCompanyName
    @param:Schema(description = "Nome fantasia ou principal da empresa.", example = "Restaurante Sabor do Brasil", required = true)
    val name: String,
    @field:Email(message = "O formato do email é inválido.")
    @param:Schema(description = "Email principal de contato da empresa.", example = "contato@sabordobrasil.com")
    val email: String? = null,
    @param:Schema(description = "Telefone principal de contato da empresa.", example = "+5511912345678")
    val phone: String? = null,
    @field:Size(min = 14, max = 18, message = "O CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX.")
    @param:Schema(description = "CNPJ da empresa.", example = "12.345.678/0001-99")
    val cnpj: String? = null,
    @param:Schema(description = "Descrição sobre a empresa ou restaurante.", example = "Especializado em culinária brasileira regional.")
    val description: String? = null,
    @field:NotNull(message = "O tipo de empresa é obrigatório.")
    @param:Schema(description = "tipo de empresa (ex: RESTAURANTE).", required = true)
    val type: CompanyTypeEnum,
    @param:Schema(description = "Indica se a empresa é publicamente listável.", example = "true", defaultValue = "true")
    val isPublic: Boolean = true,
)
