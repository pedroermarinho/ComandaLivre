package io.github.pedroermarinho.comandalivre.domain.response.product

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "Representação detalhada de um produto no cardápio.")
data class ProductResponse(
    @param:Schema(description = "ID público do produto.")
    val id: UUID,
    @param:Schema(description = "Nome do produto.")
    val name: String,
    @param:Schema(description = "Preço do produto.")
    val price: BigDecimal,
    @param:Schema(description = "Descrição detalhada do produto.")
    val description: String?,
    @param:Schema(description = "Indica se o produto está disponível para venda.")
    val availability: Boolean,
    @param:Schema(description = "URL da imagem principal do produto.")
    val image: String?,
    @param:Schema(description = "Número estimado de pessoas que o prato serve.")
    val servesPersons: Int?,
    @param:Schema(description = "Categoria do produto.")
    val category: ProductCategoryResponse,
    @param:Schema(description = "Lista de ingredientes do produto.")
    val ingredients: List<String>?,
    @param:Schema(description = "Data e hora de criação do produto.")
    val createdAt: LocalDateTime,
)
