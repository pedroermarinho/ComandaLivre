package io.github.pedroermarinho.comandalivre.domain.response.product

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class ProductWithModifiersResponse(
    val id: UUID,
    val name: String,
    val price: BigDecimal,
    val description: String?,
    val availability: Boolean,
    val image: String?,
    val servesPersons: Int?,
    val category: ProductCategoryResponse,
    val ingredients: List<String>?,
    val createdAt: LocalDateTime,
    val modifierGroups: List<ProductModifierGroupResponse>,
)
