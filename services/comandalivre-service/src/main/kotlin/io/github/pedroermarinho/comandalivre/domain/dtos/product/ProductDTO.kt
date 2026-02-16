package io.github.pedroermarinho.comandalivre.domain.dtos.product

import io.github.pedroermarinho.comandalivreapi.company.core.domain.dtos.company.CompanyDTO
import io.github.pedroermarinho.shared.valueobject.EntityId
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class ProductDTO(
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
)
