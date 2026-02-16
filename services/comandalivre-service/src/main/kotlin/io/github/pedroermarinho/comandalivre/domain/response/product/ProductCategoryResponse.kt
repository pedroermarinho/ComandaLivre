package io.github.pedroermarinho.comandalivre.domain.response.product

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Representação de uma categoria de produto.")
data class ProductCategoryResponse(
    @param:Schema(description = "ID público da categoria.")
    val id: UUID,
    @param:Schema(description = "Nome da categoria (ex: Bebidas, Pizzas, Sobremesas).")
    val name: String,
    @param:Schema(description = "Chave textual única da categoria (ex: BEBIDAS, PIZZAS).")
    val key: String,
    @param:Schema(description = "Descrição da categoria.")
    val description: String?,
)
