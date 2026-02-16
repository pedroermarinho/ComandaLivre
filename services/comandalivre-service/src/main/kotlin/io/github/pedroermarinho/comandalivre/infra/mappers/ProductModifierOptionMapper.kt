package io.github.pedroermarinho.comandalivre.infra.mappers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.dtos.product.ProductModifierOptionDTO
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.entities.ProductModifierOptionEntity
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductModifierOptionResponse
import org.springframework.stereotype.Component

@Component
class ProductModifierOptionMapper {
    fun toDTO(
        entity: ProductModifierOptionEntity,
        image: String?,
    ) = ProductModifierOptionDTO(
        id = entity.id,
        name = entity.name,
        priceChange = entity.priceChange,
        isDefault = entity.isDefault,
        displayOrder = entity.displayOrder,
        image = image,
    )

    fun toResponse(dto: ProductModifierOptionDTO) =
        ProductModifierOptionResponse(
            id = dto.id.publicId,
            name = dto.name,
            priceChange = dto.priceChange,
            isDefault = dto.isDefault,
            displayOrder = dto.displayOrder,
            image = dto.image,
        )
}
