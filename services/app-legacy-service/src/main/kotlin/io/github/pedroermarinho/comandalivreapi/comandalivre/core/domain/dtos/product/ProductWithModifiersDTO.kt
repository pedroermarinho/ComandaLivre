package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductWithModifiersDTO(
    val id: EntityId,
    val name: String,
    val price: BigDecimal,
    val description: String?,
    val availability: Boolean,
    val image: String?,
    val servesPersons: Int?,
    val category: ProductCategoryDTO,
    val company: CompanyDTO,
    val ingredients: List<String>?,
    val createdAt: LocalDateTime,
    val modifierGroups: List<ProductModifierGroupDTO>,
)
