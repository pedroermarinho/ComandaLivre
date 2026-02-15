package io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductModifierGroupDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductWithModifiersDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductWithModifiersResponse
import org.springframework.stereotype.Component

@Component
class ProductWithModifiersMapper(
    private val productCategoryMapper: ProductCategoryMapper,
    private val productModifierGroupMapper: ProductModifierGroupMapper,
) {
    fun toDTO(
        product: ProductDTO,
        modifierGroups: List<ProductModifierGroupDTO>,
    ) = ProductWithModifiersDTO(
        id = product.id,
        name = product.name,
        price = product.price,
        description = product.description,
        availability = product.availability,
        image = product.image,
        servesPersons = product.servesPersons,
        category = product.category,
        company = product.company,
        ingredients = product.ingredients,
        createdAt = product.createdAt,
        modifierGroups = modifierGroups,
    )

    fun toResponse(dto: ProductWithModifiersDTO) =
        ProductWithModifiersResponse(
            id = dto.id.publicId,
            name = dto.name,
            price = dto.price,
            description = dto.description,
            availability = dto.availability,
            image = dto.image,
            servesPersons = dto.servesPersons,
            category = productCategoryMapper.toResponse(dto.category),
            ingredients = dto.ingredients,
            createdAt = dto.createdAt,
            modifierGroups = dto.modifierGroups.map { productModifierGroupMapper.toResponse(it) },
        )
}
