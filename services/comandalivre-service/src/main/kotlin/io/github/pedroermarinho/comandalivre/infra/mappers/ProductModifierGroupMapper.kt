package io.github.pedroermarinho.comandalivre.infra.mappers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductModifierGroupDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductModifierOptionDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierGroupEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductModifierGroupResponse
import org.springframework.stereotype.Component

@Component
class ProductModifierGroupMapper(
    private val productModifierOptionMapper: ProductModifierOptionMapper,
) {
    fun toDTO(
        entity: ProductModifierGroupEntity,
        options: List<ProductModifierOptionDTO>,
    ) = ProductModifierGroupDTO(
        id = entity.id,
        name = entity.name,
        minSelection = entity.minSelection,
        maxSelection = entity.maxSelection,
        displayOrder = entity.displayOrder,
        options = options,
    )

    fun toResponse(dto: ProductModifierGroupDTO) =
        ProductModifierGroupResponse(
            id = dto.id.publicId,
            name = dto.name,
            minSelection = dto.minSelection,
            maxSelection = dto.maxSelection,
            displayOrder = dto.displayOrder,
            options = dto.options.map { productModifierOptionMapper.toResponse(it) },
        )
}
