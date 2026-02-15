package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "Formulário para a atualização de um produto existente no cardápio.")
data class ProductUpdateRequest(
    @field:NotBlank(message = "O nome do produto é obrigatório.")
    @param:Schema(description = "Novo nome do produto.", example = "Camarão na Moranga com Catupiry")
    val name: String,
    @field:NotNull(message = "O preço do produto é obrigatório.")
    @field:Positive(message = "O preço deve ser um valor positivo.")
    @param:Schema(description = "Novo preço do produto.", example = "95.50")
    val price: BigDecimal,
    @field:NotNull(message = "A categoria do produto é obrigatória.")
    @param:Schema(description = "Novo ID público da categoria do produto.")
    val categoryId: UUID,
    @param:Schema(
        description = "Nova descrição detalhada do produto.",
        example = "Nosso clássico camarão na moranga, agora com um toque especial de Catupiry Original.",
    )
    val description: String? = null,
    @field:Positive(message = "O número de pessoas que o prato serve deve ser um valor positivo.")
    @param:Schema(description = "Novo número de pessoas que o prato serve.", example = "3")
    val servesPersons: Int = 1,
    @field:NotNull(message = "A disponibilidade do produto é obrigatória.")
    @param:Schema(description = "Define se o produto está disponível para venda.", defaultValue = "true")
    val availability: Boolean = true,
    @param:Schema(
        description = "Nova lista de ingredientes do produto.",
        example = "[\"camarão\", \"moranga\", \"queijo\", \"catupiry\"]",
    )
    val ingredients: List<String>? = null,
)
