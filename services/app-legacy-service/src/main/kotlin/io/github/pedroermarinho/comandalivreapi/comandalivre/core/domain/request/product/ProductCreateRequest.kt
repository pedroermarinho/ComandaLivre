package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "Formulário para criação de um novo produto no cardápio.")
data class ProductCreateRequest(
    @param:Schema(description = "ID público do produto. Gerado automaticamente se não for fornecido.")
    val publicId: UUID? = null,
    @field:NotBlank(message = "O nome do produto é obrigatório.")
    @param:Schema(description = "Nome do produto.", example = "Camarão na Moranga", required = true)
    val name: String,
    @field:NotNull(message = "O preço do produto é obrigatório.")
    @field:Positive(message = "O preço deve ser um valor positivo.")
    @param:Schema(description = "Preço do produto.", example = "89.90", required = true)
    val price: BigDecimal,
    @field:NotNull(message = "A categoria do produto é obrigatória.")
    @param:Schema(description = "ID público da categoria do produto.", required = true)
    val categoryId: UUID,
    @param:Schema(
        description = "Descrição detalhada do produto.",
        example = "Camarão com molho de queijo, servido em uma moranga.",
    )
    val description: String? = null,
    @field:Positive(message = "O número de pessoas que o prato serve deve ser um valor positivo.")
    @param:Schema(description = "Número de pessoas que o prato serve.", example = "2")
    val servesPersons: Int? = null,
    @field:NotNull(message = "A disponibilidade do produto é obrigatória.")
    @param:Schema(description = "Indica se o produto está disponível para venda.", defaultValue = "true")
    val availability: Boolean = true,
    @field:NotNull(message = "O ID da empresa é obrigatório.")
    @param:Schema(description = "ID público da empresa à qual o produto pertence.", required = true)
    val companyId: UUID,
    @param:Schema(
        description = "Lista de ingredientes do produto.",
        example = "[\"camarão\", \"moranga\", \"queijo\"]",
    )
    val ingredients: List<String>? = null,
)
